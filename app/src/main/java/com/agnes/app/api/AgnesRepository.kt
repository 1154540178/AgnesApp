package com.agnes.app.api

import com.agnes.app.model.*
import kotlinx.coroutines.delay
import retrofit2.Response

class AgnesRepository {

    private val api = ApiClient.getService()

    /** 发送聊天消息 */
    suspend fun sendMessage(content: String): Result<String> {
        return try {
            val messages = listOf(
                ChatMessage("user", content)
            )
            val request = ChatRequest(messages = messages)
            val response = api.chat(request)

            if (response.isSuccessful && response.body()?.choices?.isNotEmpty() == true) {
                val reply = response.body()!!.choices[0].message?.content ?: ""
                Result.success(reply)
            } else {
                val errorMsg = "API 错误: ${response.code()} ${response.message()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 生成图片 */
    suspend fun generateImage(prompt: String, size: String = "1024x1024", count: Int = 1): Result<List<com.agnes.app.model.Message.ImageResult>> {
        return try {
            val request = com.agnes.app.model.ImageRequest(
                prompt = prompt,
                size = size,
                n = count
            )
            val response = api.generateImage(request)

            if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
                val results = response.body()!!.data.map { imageData ->
                    com.agnes.app.model.Message.ImageResult(
                        imageUrl = imageData.url ?: imageData.revised_url ?: "",
                        prompt = imageData.prompt ?: prompt
                    )
                }
                Result.success(results)
            } else {
                val errorMsg = "图片生成失败: ${response.code()} ${response.message()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 生成视频（异步任务） */
    suspend fun generateVideo(prompt: String, image: String? = null): Result<String> {
        return try {
            val request = com.agnes.app.model.VideoRequest(
                prompt = prompt,
                image = image,
                `num_frames` = 121,
                frame_rate = 24
            )
            val response = api.generateVideo(request)

            if (response.isSuccessful && response.body()?.data?.id != null) {
                Result.success(response.body()!!.data.id!!)
            } else {
                val errorMsg = "视频创建失败: ${response.code()} ${response.message()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 轮询视频任务状态 */
    suspend fun pollVideoTask(taskId: String, maxAttempts: Int = 60, intervalMs: Long = 5000L): Result<String> {
        for (i in 0 until maxAttempts) {
            try {
                val response = api.getVideoTaskStatus(taskId)
                if (response.isSuccessful && response.body() != null) {
                    val task = response.body()!!
                    when (task.status) {
                        "completed" -> {
                            return if (task.result_url != null) {
                                Result.success(task.result_url)
                            } else {
                                Result.failure(Exception("视频生成完成但未返回 URL"))
                            }
                        }
                        "failed" -> {
                            return Result.failure(Exception("视频生成失败"))
                        }
                        "pending", "processing" -> {
                            delay(intervalMs)
                        }
                        else -> {
                            delay(intervalMs)
                        }
                    }
                } else {
                    delay(intervalMs)
                }
            } catch (e: Exception) {
                delay(intervalMs)
            }
        }
        return Result.failure(Exception("视频生成超时"))
    }

    /** 测试 API 连接 */
    suspend fun testConnection(): Result<Unit> {
        return try {
            val messages = listOf(ChatMessage("user", "Hello"))
            val request = ChatRequest(messages = messages, model = "agnes-2.0-flash")
            val response = api.chat(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("API 错误: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
