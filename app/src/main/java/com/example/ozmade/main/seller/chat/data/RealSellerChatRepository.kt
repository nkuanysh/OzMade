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

        chats.map { chat ->
            val last = chat.messages?.maxByOrNull { it.createdAt }
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
        val resp = api.getSellerChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")
        val dtos = resp.body().orEmpty()

        val myId = sessionStore.myUserId()
        Log.d("SellerChatRepo", "myId: $myId")

        dtos.map { dto ->
            // УЛУЧШЕННАЯ ЛОГИКА ДЛЯ ПРОДАВЦА:
            // 1. По ID (если он есть)
            // 2. Если ID нет, по роли. 
            // В чате ПРОДАВЦА роль "SELLER" (или любая кроме BUYER) - это МЫ.
            val role = dto.senderRole?.uppercase()
            val isMine = if (myId != null && myId > 0) {
                dto.senderId == myId
            } else {
                role != "BUYER"
            }
            
            Log.d("SellerChatRepo", "Msg ${dto.id}: role=$role, isMine=$isMine")

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
