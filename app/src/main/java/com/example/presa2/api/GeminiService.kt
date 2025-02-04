package com.example.presa2.api

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(private val apiKey: String) {
    private val model = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = apiKey
    )

    suspend fun getRecommendations(
        temperature: Float,
        rainfall: Float,
        fertilizer: Boolean,
        irrigation: Boolean,
        days: Int,
        predictedYield: Float
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            As an agricultural expert specializing in strawberry farming in La Trinidad, Benguet, Philippines, provide specific recommendations based on the following conditions:
            
            Current Conditions:
            - Temperature: ${temperature}°C
            - Rainfall: ${rainfall}mm (last 3 hours)
            - Fertilizer Use: ${if (fertilizer) "Yes" else "No"}
            - Irrigation System: ${if (irrigation) "Yes" else "No"}
            - Growing Period: ${days} days
            - Predicted Yield: ${String.format("%.2f", predictedYield)} kg
            
            Consider these specific factors for La Trinidad, Benguet:
            - Optimal temperature range for strawberries: 15-25°C
            - Typical rainfall patterns in Benguet
            - Local soil conditions (typically acidic)
            - High altitude farming conditions
            - Common pests and diseases in the region
            
            Provide personalized recommendations focusing on:
            1. Temperature management (if outside optimal range)
            2. Water management based on current rainfall
            3. Fertilizer adjustments if needed
            4. Pest and disease prevention specific to Benguet
            5. Yield improvement suggestions
            
            Keep the response concise and actionable, focusing on the most critical factors that need attention based on the current conditions.
        """.trimIndent()

        try {
            val response = model.generateContent(prompt)
            response.text ?: "Unable to generate recommendations at this time."
        } catch (e: Exception) {
            when {
                e.message?.contains("quota") == true -> 
                    "Unable to get AI recommendations at this time. The API quota has been exceeded. Please try again later."
                e.message?.contains("rate_limit") == true ->
                    "Too many requests. Please wait a moment and try again."
                e.message?.contains("invalid_api_key") == true ->
                    "Authentication error. Please check the API configuration."
                else -> "Error generating recommendations: ${e.message}"
            }
        }
    }
}
