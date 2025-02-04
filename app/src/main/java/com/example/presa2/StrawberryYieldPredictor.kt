package com.example.presa2

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.max

class StrawberryYieldPredictor(private val context: Context) {
    private var interpreter: Interpreter? = null
    private val modelFile = "model.tflite"
    private val inputSize = 5

    // Normalization parameters
    private val tempRange = Pair(10.0f, 35.0f)  // Expected temperature range in Celsius
    private val rainfallRange = Pair(0.0f, 500.0f)  // Expected rainfall range in mm
    private val daysRange = Pair(30.0f, 90.0f)  // Growing period range
    
    init {
        try {
            val modelBuffer = loadModelFile()
            interpreter = Interpreter(modelBuffer)
        } catch (e: Exception) {
            Log.e("Predictor", "Error initializing model", e)
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelFile)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return inputStream.channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun normalize(value: Float, range: Pair<Float, Float>): Float {
        return (value - range.first) / (range.second - range.first)
    }

    private fun denormalize(value: Float, range: Pair<Float, Float>): Float {
        return value * (range.second - range.first) + range.first
    }

    fun predict(
        temperature: Float,
        rainfall: Float,
        fertilizer: Float,
        irrigation: Float,
        days: Int
    ): Float {
        val inputData = floatArrayOf(
            normalize(temperature, tempRange),
            normalize(rainfall, rainfallRange),
            fertilizer,
            irrigation,
            normalize(days.toFloat(), daysRange)
        )
        
        // Create input buffer
        val inputBuffer = ByteBuffer.allocateDirect(inputSize * 4) // 4 bytes per float
        inputBuffer.order(ByteOrder.nativeOrder())
        
        // Add normalized inputs
        inputData.forEach { inputBuffer.putFloat(it) }
        
        // Reset position to start
        inputBuffer.rewind()
        
        // Create output buffer
        val outputBuffer = ByteBuffer.allocateDirect(4) // 1 float output
        outputBuffer.order(ByteOrder.nativeOrder())
        
        // Run inference
        interpreter?.run(inputBuffer, outputBuffer)
        
        // Get result and ensure non-negative output
        outputBuffer.rewind()
        val prediction = outputBuffer.float
        
        // Log the prediction details for debugging
        Log.d("Predictor", """
            Input: temp=$temperature, rain=$rainfall, fert=$fertilizer, irr=$irrigation, days=$days
            Normalized: temp=${inputData[0]}, rain=${inputData[1]}, fert=${inputData[2]}, irr=${inputData[3]}, days=${inputData[4]}
            Raw prediction: $prediction
        """.trimIndent())
        
        return max(0f, prediction)  // Ensure non-negative yield
    }

    fun close() {
        interpreter?.close()
    }
}
