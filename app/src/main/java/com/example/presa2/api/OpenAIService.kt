package com.example.presa2.api

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kotlin.time.Duration.Companion.seconds

class OpenAIService(apiKey: String) {
    private val openAI = OpenAI(
        config = OpenAIConfig(
            token = apiKey,
            timeout = Timeout(socket = 60.seconds)
        )
    )

    suspend fun getRecommendations(
        temperature: Float,
        rainfall: Float,
        fertilizer: Boolean,
        irrigation: Boolean,
        days: Int,
        yield: Float
    ): String {
        val prompt = """
            As an AI agricultural expert, provide specific recommendations for strawberry farming in La Trinidad, Benguet based on these conditions:
            
            Current Conditions:
            - Temperature: $temperature°C
            - Rainfall: $rainfall mm
            - Fertilizer Use: ${if (fertilizer) "Yes" else "No"}
            - Irrigation System: ${if (irrigation) "Yes" else "No"}
            - Growing Period: $days days
            - Predicted Yield: $yield kg
            
            Please provide 3-4 specific, actionable recommendations focusing on:
            1. Temperature management
            2. Water management
            3. Fertilization (if needed)
            4. General cultivation practices
            
            Format the response in clear, concise bullet points.
        """.trimIndent()

        return try {
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.System,
                        content = "You are an expert agricultural AI assistant specializing in strawberry cultivation in La Trinidad, Benguet. Provide specific, practical advice based on local conditions."
                    ),
                    ChatMessage(
                        role = ChatRole.User,
                        content = prompt
                    )
                )
            )

            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
            completion.choices.first().message.content.toString()
        } catch (e: Exception) {
            when {
                e.message?.contains("quota") == true -> 
                    "Unable to get AI recommendations at this time. The API quota has been exceeded. Please try again later or contact support for assistance."
                e.message?.contains("rate_limit") == true ->
                    "Too many requests. Please wait a moment and try again."
                e.message?.contains("invalid_api_key") == true ->
                    "Authentication error. Please check the API configuration."
                else -> "Unable to get AI recommendations. Please try again later. Error: ${e.message}"
            }
        }
    }
}
