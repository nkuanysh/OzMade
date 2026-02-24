package com.example.ozmade.main.seller.chat.data

interface SellerChatRepository {
    suspend fun getThreads(): List<SellerChatThreadUi>
    suspend fun getMessages(chatId: Int): List<SellerChatMessageUi>
    suspend fun sendMessage(chatId: Int, text: String)
}