package com.example.ozmade.main.seller.chat.data

import android.util.Log
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.OffsetDateTime
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.auth.SessionStore
import com.example.ozmade.network.model.ChatSendMessageRequest
import com.example.ozmade.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealSellerChatRepository @Inject constructor(
    private val api: OzMadeApi,
    private val sessionStore: SessionStore
) : SellerChatRepository {

    private val TAG = "RealSellerChatRepo"

    override suspend fun getThreads(): List<SellerChatThreadUi> = withContext(Dispatchers.IO) {
        val resp = api.getSellerChats()
        if (!resp.isSuccessful) error("Не удалось загрузить чаты (${resp.code()})")
        
        val myId = sessionStore.myUserId()
        val chats = resp.body().orEmpty()

        Log.d(TAG, "getThreads: myId=$myId, received ${chats.size} chats")

        // Filter: for seller view, we only show chats where we are the seller
        // and ignore chats where we are the buyer
        val filtered = if (myId != null && myId > 0) {
            chats.filter { it.sellerId == myId && it.buyerId != myId }
        } else {
            chats
        }

        filtered.mapNotNull { chat ->
            // Если чат удален продавцом полностью, не показываем его
            if (chat.deletedBySeller) return@mapNotNull null

            // Фильтруем сообщения по времени очистки
            val messages = chat.messages.orEmpty()
            val visibleMessages = messages.filter { msg ->
                chat.sellerClearedAt.isNullOrEmpty() || chat.sellerClearedAt.startsWith("0001") || msg.createdAt > chat.sellerClearedAt
            }

            // Fallback to any message if visible list is empty but chat exists
            val last = visibleMessages.maxByOrNull { it.id } ?: messages.maxByOrNull { it.id }
            
            SellerChatThreadUi(
                chatId = chat.id,
                buyerId = chat.buyerId,
                buyerName = chat.buyerName ?: "Покупатель #${chat.buyerId}",
                buyerPhotoUrl = ImageUtils.formatProfilePhotoUrl(chat.buyerPhoto),
                productId = chat.productId ?: 0,
                productTitle = chat.productName ?: "Без названия",
                productImageUrl = ImageUtils.formatImageUrl(chat.productImage),
                lastMessage = last?.content ?: "Нет новых сообщений",
                lastTimeText = formatTime(last?.createdAt ?: chat.updatedAt ?: chat.createdAt)
            )
        }
    }

    override suspend fun getMessages(chatId: Int): List<SellerChatMessageUi> = withContext(Dispatchers.IO) {
        val chatsResp = api.getSellerChats()
        val chat = chatsResp.body()?.find { it.id == chatId }
        val clearedAt = chat?.sellerClearedAt

        val resp = api.getSellerChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")
        val dtos = resp.body().orEmpty()

        val myId = sessionStore.myUserId()

        Log.d(TAG, "getMessages: chatId=$chatId, myId=$myId, total=${dtos.size}, clearedAt=$clearedAt")

        dtos
            .filter { clearedAt.isNullOrEmpty() || clearedAt.startsWith("0001") || it.createdAt > clearedAt }
            .map { dto ->
                val role = dto.senderRole?.uppercase()
                val isMine = if (myId != null && myId > 0) {
                    dto.senderId == myId
                } else {
                    role != "BUYER" && role != "USER"
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

    private fun formatTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return ""
        return try {
            // Backend provides ISO 8601 like "2023-10-27T10:00:00Z" (UTC)
            val odt = OffsetDateTime.parse(isoString)
            val local = odt.atZoneSameInstant(ZoneId.systemDefault())
            local.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            try {
                // Fallback for simple parts if parsing fails
                val parts = isoString.split("T")
                if (parts.size < 2) return isoString
                parts[1].take(5)
            } catch (e2: Exception) {
                isoString ?: ""
            }
        }
    }
}
