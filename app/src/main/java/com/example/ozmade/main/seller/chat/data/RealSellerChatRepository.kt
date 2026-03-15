package com.example.ozmade.main.seller.chat.data

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
        // ✅ FIXED: Call /seller/chats (seller endpoint, not /chats)
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
                lastTimeText = last?.createdAt ?: ""
            )
        }
    }

    override suspend fun getMessages(chatId: Int): List<SellerChatMessageUi> = withContext(Dispatchers.IO) {
        // ✅ FIXED: Call /seller/chats/:chat_id/messages (seller endpoint)
        val resp = api.getSellerChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")
        val dtos = resp.body().orEmpty()

        dtos.map { dto ->
            SellerChatMessageUi(
                id = dto.id,
                text = dto.content,
                isMine = (dto.senderId == sessionStore.myUserId()),
                timeText = dto.createdAt
            )
        }
    }

    override suspend fun sendMessage(chatId: Int, text: String) = withContext(Dispatchers.IO) {
        // ✅ FIXED: Call /seller/chats/:chat_id/messages (seller endpoint for sending)
        val resp = api.sendSellerChatMessage(chatId, ChatSendMessageRequest(content = text))
        if (!resp.isSuccessful) error("Не удалось отправить (${resp.code()})")
    }
}