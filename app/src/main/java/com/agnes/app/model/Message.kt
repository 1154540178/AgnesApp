package com.agnes.app.model

/** 聊天消息类型 */
sealed class Message {
    data class User(val content: String, val timestamp: Long = System.currentTimeMillis()) : Message()
    data class AI(val content: String, val timestamp: Long = System.currentTimeMillis()) : Message()
    data class ImageResult(val imageUrl: String, val prompt: String, val timestamp: Long = System.currentTimeMillis()) : Message()
    data class VideoResult(val videoUrl: String, val prompt: String, val thumbnailUrl: String? = null, val timestamp: Long = System.currentTimeMillis()) : Message()
    data class Error(val message: String, val timestamp: Long = System.currentTimeMillis()) : Message()
}

/** OpenAI 兼容的请求/响应模型 */
data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String = "agnes-2.0-flash",
    val messages: List<ChatMessage>,
    val stream: Boolean = false
)

data class ChatResponse(
    val choices: List<Choice>?
) {
    data class Choice(
        val index: Int = 0,
        val message: MessageItem? = null
    ) {
        data class MessageItem(
            val role: String = "assistant",
            val content: String? = ""
        )
    }
}

data class ImageRequest(
    val model: String = "agnes-image-2.1-flash",
    val prompt: String,
    val size: String = "1024x1024",
    val n: Int = 1
)

data class ImageResponse(
    val data: List<ImageData>?
) {
    data class ImageData(
        val url: String? = null,
        val revised_url: String? = null,
        val prompt: String? = null
    )
}

data class VideoRequest(
    val model: String = "agnes-video-v2.0",
    val prompt: String,
    val image: String? = null,
    val `num_frames`: Int = 121,
    val frame_rate: Int = 24
)

data class VideoResponse(
    val data: VideoData?
) {
    data class VideoData(
        val id: String? = null,
        val url: String? = null,
        val status: String = "pending"
    )
}

/** 轮询视频任务状态 */
data class VideoTaskStatus(
    val id: String? = null,
    val status: String = "pending",
    val progress: Int = 0,
    val result_url: String? = null
)
