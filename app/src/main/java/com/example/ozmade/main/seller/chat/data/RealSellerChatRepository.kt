package com.example.ozmade.main.seller.chat.data

import android.util.Log
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.auth.SessionStore
import com.example.ozmade.network.model.ChatSendMessageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealSellerChatRepository @Inject constructor(
    private val api: OzMadeApi,
    private val sessionStore: SessionStore
) : SellerChatRepository {

    override suspend fun getThreads(): List<SellerChatThreadUi> = withContext(Dispatchers.IO) {
        val resp = api.getSellerChats()
        if (!resp.isSuccessful) error("Не удалось загрузить чаты (${resp.code()})")
        val chats = resp.body().orEmpty()

        chats.mapNotNull { chat ->
            // Фильтруем сообщения: только те, что созданы после последней очистки истории продавцом
            val visibleMessages = chat.messages.orEmpty().filter { msg ->
                chat.sellerClearedAt == null || msg.createdAt > chat.sellerClearedAt
            }

            // Если после фильтрации сообщений не осталось, и чат был очищен/удален, не показываем его в списке
            if (visibleMessages.isEmpty()) return@mapNotNull null

            val last = visibleMessages.maxByOrNull { it.createdAt }
            SellerChatThreadUi(
                chatId = chat.id,
                buyerId = chat.buyerId,
                buyerName = "Покупатель #${chat.buyerId}",
                lastMessage = last?.content ?: "",
                lastTimeText = formatTime(last?.createdAt ?: "")
            )
        }
    }

    override suspend fun getMessages(chatId: Int): List<SellerChatMessageUi> = withContext(Dispatchers.IO) {
        // Получаем информацию о чате, чтобы узнать время очистки (sellerClearedAt)
        val chatsResp = api.getSellerChats()
        val chat = chatsResp.body()?.find { it.id == chatId }
        val clearedAt = chat?.sellerClearedAt

        val resp = api.getSellerChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")
        val dtos = resp.body().orEmpty()

        val myId = sessionStore.myUserId()

        dtos
            .filter { clearedAt == null || it.createdAt > clearedAt }
            .map { dto ->
                val role = dto.senderRole?.uppercase()
                val isMine = if (myId != null && myId > 0) {
                    dto.senderId == myId
                } else {
                    role != "BUYER"
                }

                SellerChatMessageUi(
                    id = dto.id,
                    text = dto.content,
                    isMine = isMine,
                    timeText = formatTime(dto.createdAt)
                )
            }
    }

    override suspend fun sendMessage(chatId: Int, text: String) = withContext(Dispatchers.IO) {
        val resp = api.sendSellerChatMessage(chatId, ChatSendMessageRequest(content = text))
        if (!resp.isSuccessful) error("Не удалось отправить (${resp.code()})")
    }

    override suspend fun deleteChat(chatId: Int) = withContext(Dispatchers.IO) {
        val resp = api.deleteChat(chatId)
        if (!resp.isSuccessful) error("Не удалось удалить историю (${resp.code()})")
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
