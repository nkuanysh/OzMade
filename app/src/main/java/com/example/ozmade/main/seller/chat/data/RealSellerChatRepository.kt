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
        val resp = api.getBuyerChats()
        if (!resp.isSuccessful) error("Не удалось загрузить чаты (${resp.code()})")
        val chats = resp.body().orEmpty()

        chats.map { chat ->
            val last = chat.messages?.maxByOrNull { it.createdAt } // ISO строка, обычно ок для сортировки
            SellerChatThreadUi(
                chatId = chat.id,
                buyerId = chat.buyerId,
                buyerName = "Покупатель #${chat.buyerId}", // ✅ пока так, имени у тебя нет в DTO
                lastMessage = last?.content ?: "",
                lastTimeText = last?.createdAt ?: ""       // ✅ пока так, красивое время сделаем позже
            )
        }
    }

    override suspend fun getMessages(chatId: Int): List<SellerChatMessageUi> = withContext(Dispatchers.IO) {
        val resp = api.getChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")
        val dtos = resp.body().orEmpty()

        dtos.map { dto ->
            SellerChatMessageUi(
                id = dto.id.toString(),
                text = dto.content,
                isMine = (dto.senderRole == "SELLER"),
                timeText = dto.createdAt
            )
        }
    }

    override suspend fun sendMessage(chatId: Int, text: String) = withContext(Dispatchers.IO) {
        // ⚠️ Этот метод в api нужно добавить (см. пункт C)
        val resp = api.sendChatMessage(chatId, ChatSendMessageRequest(content = text))
        if (!resp.isSuccessful) error("Не удалось отправить (${resp.code()})")
    }
}