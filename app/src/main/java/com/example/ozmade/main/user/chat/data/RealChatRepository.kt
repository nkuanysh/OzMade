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
        val resp = api.getBuyerChats()
        if (!resp.isSuccessful) error("Не удалось загрузить чаты (${resp.code()})")
        val chats = resp.body().orEmpty()

        chats.map { c ->
            val last = c.messages?.maxByOrNull { it.createdAt }
            ChatThreadUi(
                chatId = c.id,
                sellerId = c.sellerId,
                sellerName = "Продавец #${c.sellerId}", // имя появится когда бэк добавит поле
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
        val resp = api.getChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")
        val myId = sessionStore.myUserId()

        resp.body().orEmpty().map { dto ->
            ChatMessageUi(
                id = dto.id.toString(),
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
            val resp = api.createChatOrGetExisting(
                CreateChatRequest(productId = productId, content = content)
            )
            if (!resp.isSuccessful) error("Не удалось создать чат (${resp.code()})")
            val chat = resp.body() ?: error("Пустой ответ createChat")
            return@withContext chat.id
        } else {
            val resp = api.sendChatMessage(
                existingChatId,
                ChatSendMessageRequest(content = content)
            )
            if (!resp.isSuccessful) error("Не удалось отправить (${resp.code()})")
            return@withContext existingChatId
        }
    }
}