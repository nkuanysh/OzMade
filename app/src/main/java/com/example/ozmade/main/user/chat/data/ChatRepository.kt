package com.example.ozmade.main.user.chat.data

interface ChatRepository {
    suspend fun getThreads(): List<ChatThreadUi>
    suspend fun getMessages(chatId: Int): List<ChatMessageUi>

    suspend fun findChatIdOrNull(productId: Int): Int?

    suspend fun sendMessageOrCreate(
        productId: Int,
        content: String,
        existingChatId: Int?
    ): Int

}
