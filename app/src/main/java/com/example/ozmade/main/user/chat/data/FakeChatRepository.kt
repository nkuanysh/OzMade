/*
package com.example.ozmade.main.user.chat.data

import com.example.ozmade.main.user.chat.data.ChatMessageUi
import com.example.ozmade.main.user.chat.data.ChatThreadUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeChatRepository @Inject constructor() : ChatRepository {

    override suspend fun getThreads(): List<ChatThreadUi> = emptyList()
    override suspend fun getMessages(chatId: Int): List<ChatMessageUi> = emptyList()

    override suspend fun sendMessageOrCreate(
        productId: Int,
        content: String,
        existingChatId: Int?,
        sellerId: Int?
    ): Int = 0

    override suspend fun deleteChat(chatId: Int) {}
}
*/
