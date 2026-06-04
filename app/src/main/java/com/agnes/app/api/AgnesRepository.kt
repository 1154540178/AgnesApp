package com.agnes.app.api

import com.agnes.app.model.*
import kotlinx.coroutines.delay
import retrofit2.Response

class AgnesRepository {

    private val api = ApiClient.getService()

    suspend fun sendMessage(content: String): Result<String> {
        return try {
            val messages = listOf(ChatMessage("user", content))
            val request = ChatRequest(messages = messages)
            val response = api.chat(request)

            if (response.isSuccessful) {
                val body = response.body()
                val reply = body?.choices?.firstOrNull()?.message?.content ?: ""
                Result.success(reply)
            } else {
                val errorMsg = "API error: ${response.code()} ${response.message()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateImage(prompt: String, size: String = "1024x1024", count: Int = 1): Result<List<Message.ImageResult>> {
        return try {
            val request = ImageRequest(prompt = prompt, size = size, n = count)
            val response = api.generateImage(request)

            if (response.isSuccessful) {
                val data = response.body()?.data
                val results = data?.map { imageData ->
                    Message.ImageResult(
                        imageUrl = imageData.url ?: imageData.revised_url ?: "",
                        prompt = imageData.prompt ?: prompt
                    )
                } ?: emptyList()
                Result.success(results)
            } else {
                Result.failure(Exception("Image generation failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateVideo(prompt: String, image: String? = null): Result<String> {
        return try {
            val request = VideoRequest(prompt = prompt, image = image)
            val response = api.generateVideo(request)

            if (response.isSuccessful) {
                val taskId = response.body()?.data?.id
                if (taskId != null) {
                    Result.success(taskId)
                } else {
                    Result.failure(Exception("No task ID returned"))
                }
            } else {
                Result.failure(Exception("Video creation failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pollVideoTask(taskId: String, maxAttempts: Int = 60, intervalMs: Long = 5000L): Result<String> {
        for (i in 0 until maxAttempts) {
            try {
                val response = api.getVideoTaskStatus(taskId)
                if (response.isSuccessful) {
                    val task = response.body()
                    when (task?.status) {
                        "completed" -> {
                            val url = task.result_url
                            return if (url != null) Result.success(url)
                            else Result.failure(Exception("Video completed but no URL"))
                        }
                        "failed" -> return Result.failure(Exception("Video generation failed"))
                        "pending", "processing" -> delay(intervalMs)
                        else -> delay(intervalMs)
                    }
                } else {
                    delay(intervalMs)
                }
            } catch (e: Exception) {
                delay(intervalMs)
            }
        }
        return Result.failure(Exception("Video generation timed out"))
    }

    suspend fun testConnection(): Result<Unit> {
        return try {
            val messages = listOf(ChatMessage("user", "Hello"))
            val request = ChatRequest(messages = messages)
            val response = api.chat(request)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("API error: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
