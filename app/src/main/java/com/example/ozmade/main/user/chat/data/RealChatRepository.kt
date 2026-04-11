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

    override suspend fun getThreads(): List<ChatThreadUi> = withContext(Dispatchers.IO) {
        val resp = api.getBuyerChats()
        if (!resp.isSuccessful) error("Не удалось загрузить чаты (${resp.code()})")

        val chats = resp.body().orEmpty()

        chats.map { c ->
            val visibleMessages = c.messages.orEmpty().filter { msg ->
                c.buyerClearedAt == null || msg.createdAt > c.buyerClearedAt
            }

            val last = visibleMessages.maxByOrNull { it.createdAt }

            // Пытаемся найти имя продавца/магазина в разных полях.
            // Приоритет: 
            // 1. Прямое поле sellerName в объекте чата
            // 2. Поле name во вложенном объекте seller
            // 3. productName (если это "Чат по товару", но обычно нам нужно имя продавца)
            // 4. Заглушка
            val displayName = c.sellerName 
                ?: c.seller?.name 
                ?: "Продавец #${c.sellerId}"

            ChatThreadUi(
                chatId = c.id,
                sellerId = c.sellerId,
                sellerName = displayName,
                productId = c.productId ?: 0,
                productTitle = c.productName ?: "Без названия",
                productPrice = 0, 
                productImageUrl = c.productImage,
                lastMessage = last?.content ?: "Нет сообщений",
                lastTimeText = formatTime(last?.createdAt ?: c.createdAt),
                isOnline = false
            )
        }.sortedByDescending { it.chatId }
    }

    override suspend fun getMessages(chatId: Int): List<ChatMessageUi> = withContext(Dispatchers.IO) {
        val chatsResp = api.getBuyerChats()
        val chat = chatsResp.body()?.find { it.id == chatId }
        val clearedAt = chat?.buyerClearedAt

        val resp = api.getBuyerChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")

        val myId = sessionStore.myUserId()

        resp.body().orEmpty()
            .filter { clearedAt == null || it.createdAt > clearedAt }
            .map { dto ->
                val role = dto.senderRole?.uppercase()
                val isMine = if (myId != null && myId > 0) {
                    dto.senderId == myId
                } else {
                    role != "SELLER"
                }

                ChatMessageUi(
                    id = dto.id,
                    text = dto.content,
                    isMine = isMine,
                    timeText = formatTime(dto.createdAt)
                )
            }
    }

    override suspend fun sendMessageOrCreate(
        productId: Int,
        content: String,
        existingChatId: Int?
    ): Int = withContext(Dispatchers.IO) {

        if (existingChatId == null || existingChatId == 0) {
            val resp = api.createBuyerChat(
                CreateChatRequest(
                    productId = productId,
                    content = content
                )
            )
            if (!resp.isSuccessful) error("Не удалось создать чат (${resp.code()})")

            val chat = resp.body() ?: error("Пустой ответ createChat")
            return@withContext chat.id
        } else {
            val resp = api.sendBuyerChatMessage(
                existingChatId,
                ChatSendMessageRequest(content = content)
            )
            if (!resp.isSuccessful) error("Не удалось отправить (${resp.code()})")

            return@withContext existingChatId
        }
    }

    override suspend fun deleteChat(chatId: Int) = withContext(Dispatchers.IO) {
        val resp = api.deleteChat(chatId)
        if (!resp.isSuccessful) error("Не удалось очистить чат (${resp.code()})")
    }

    private fun formatTime(isoString: String): String {
        if (isoString.isBlank()) return ""
        return try {
            val parts = isoString.split("T")
            if (parts.size < 2) return isoString
            parts[1].take(5)
        } catch (e: Exception) {
            isoString
        }
    }
}
