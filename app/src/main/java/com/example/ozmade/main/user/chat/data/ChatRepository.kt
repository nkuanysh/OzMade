package com.example.ozmade.main.user.chat.data

interface ChatRepository {
    suspend fun getThreads(): List<ChatThreadUi>

    suspend fun getMessages(threadId: String): List<ChatMessageUi>

    suspend fun ensureThread(
        sellerId: String,
        sellerName: String,
        productId: String,
        productTitle: String,
        productPrice: Int,
        productImageUrl: String?
    ): String // returns threadId

    suspend fun sendMessage(threadId: String, text: String)
}
