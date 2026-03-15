package com.example.ozmade.main.user.chat.data

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.auth.SessionStore
import com.example.ozmade.network.model.ChatSendMessageRequest
import com.example.ozmade.network.model.CreateChatRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealChatRepository @Inject constructor(
    private val api: OzMadeApi,
    private val sessionStore: SessionStore
) : ChatRepository {

    override suspend fun getThreads() = withContext(Dispatchers.IO) {
        // ✅ FIXED: Call /chats (buyer endpoint) instead of /seller/chats
        val resp = api.getBuyerChats()
        if (!resp.isSuccessful) error("Не удалось загрузить чаты (${resp.code()})")
        val chats = resp.body().orEmpty()

        chats.map { c ->
            val last = c.messages?.maxByOrNull { it.createdAt }
            ChatThreadUi(
                chatId = c.id,
                sellerId = c.sellerId,
                sellerName = "Продавец #${c.sellerId}",
                productId = c.productId ?: 0,
                productTitle = c.productName ?: "",
                productPrice = 0,
                productImageUrl = c.productImage,
                lastMessage = last?.content ?: "",
                lastTimeText = last?.createdAt ?: ""
            )
        }
    }

    override suspend fun findChatIdOrNull(productId: Int): Int? = withContext(Dispatchers.IO) {
        val threads = getThreads()
        threads.firstOrNull { it.productId == productId }?.chatId
    }

    override suspend fun getMessages(chatId: Int) = withContext(Dispatchers.IO) {
        // ✅ FIXED: Call /chats/:chat_id/messages (buyer endpoint)
        val resp = api.getBuyerChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")
        val myId = sessionStore.myUserId()

        resp.body().orEmpty().map { dto ->
            ChatMessageUi(
                id = dto.id,
                text = dto.content,
                isMine = (myId != null && dto.senderId == myId),
                timeText = dto.createdAt
            )
        }
    }

    override suspend fun sendMessageOrCreate(
        productId: Int,
        content: String,
        existingChatId: Int?
    ): Int = withContext(Dispatchers.IO) {

        if (existingChatId == null) {
            // ✅ FIXED: Call /chats (create new chat)
            val resp = api.createBuyerChat(
                CreateChatRequest(productId = productId, content = content)
            )
            if (!resp.isSuccessful) error("Не удалось создать чат (${resp.code()})")
            val chat = resp.body() ?: error("Пустой ответ createChat")
            return@withContext chat.id
        } else {
            // ✅ FIXED: Call /chats/:chat_id/messages (send message to existing chat)
            val resp = api.sendBuyerChatMessage(
                existingChatId,
                ChatSendMessageRequest(content = content)
            )
            if (!resp.isSuccessful) error("Не удалось отправить (${resp.code()})")
            return@withContext existingChatId
        }
    }
}