package com.agnes.app.api

import com.agnes.app.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AgnesApiService {

    @POST("chat/completions")
    suspend fun chat(@Body request: ChatRequest): Response<ChatResponse>

    @POST("images/generations")
    suspend fun generateImage(@Body request: ImageRequest): Response<ImageResponse>

    @POST("videos/generations")
    suspend fun generateVideo(@Body request: VideoRequest): Response<VideoResponse>

    @GET("videos/tasks/{taskId}")
    suspend fun getVideoTaskStatus(@Path("taskId") taskId: String): Response<VideoTaskStatus>

    companion object {
        const val DEFAULT_BASE_URL = "https://api.agnes-ai.com/v1/"
    }
}
