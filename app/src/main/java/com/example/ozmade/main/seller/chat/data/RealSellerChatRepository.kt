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

        // Filter: for seller view, we show chats where we are the seller.
        // We include self-chats (buyerId == sellerId) here.
        val filtered = chats.filter { chat ->
            val isMySellerChat = if (myId != null && myId > 0) chat.sellerId == myId else true
            isMySellerChat && !chat.deletedBySeller
        }

        filtered.map { chat ->
            val visibleMessages = chat.messages.orEmpty().filter { msg ->
                chat.sellerClearedAt.isNullOrEmpty() || chat.sellerClearedAt.startsWith("0001") || msg.createdAt > chat.sellerClearedAt
            }

            val lastMsgFromVisible = visibleMessages.maxByOrNull { it.id }
            
            // Check if the chat-level last message is also "visible" (newer than clearedAt)
            val isLastMessageVisible = chat.lastMessage != null && 
                (chat.sellerClearedAt.isNullOrEmpty() || 
                 chat.sellerClearedAt.startsWith("0001") || 
                 chat.lastMessage.createdAt > chat.sellerClearedAt)

            val finalLastMessage = when {
                lastMsgFromVisible != null -> lastMsgFromVisible.content
                isLastMessageVisible -> chat.lastMessage?.content ?: chat.lastMessageContent ?: "Напишите сообщение..."
                else -> "Напишите сообщение..."
            }

            val timeSource = when {
                lastMsgFromVisible != null -> lastMsgFromVisible.createdAt
                isLastMessageVisible -> chat.lastMessage?.createdAt ?: chat.updatedAt ?: chat.createdAt
                else -> chat.updatedAt ?: chat.createdAt
            }

            SellerChatThreadUi(
                chatId = chat.id,
                buyerId = chat.buyerId,
                buyerName = chat.buyerName ?: "Покупатель #${chat.buyerId}",
                buyerPhotoUrl = ImageUtils.formatImageUrl(chat.buyerPhoto),
                productId = chat.productId ?: 0,
                productTitle = chat.productName ?: "Без названия",
                productImageUrl = ImageUtils.formatImageUrl(chat.productImage),
                lastMessage = finalLastMessage,
                lastTimeText = if (lastMsgFromVisible != null || isLastMessageVisible) formatTime(timeSource) else ""
            )
        }.sortedByDescending { it.chatId }
    }

    override suspend fun getMessages(chatId: Int): List<SellerChatMessageUi> = withContext(Dispatchers.IO) {
        val chatsResp = api.getSellerChats()
        val chat = chatsResp.body()?.find { it.id == chatId }
        val clearedAt = chat?.sellerClearedAt

        val resp = api.getSellerChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")
        val dtos = resp.body().orEmpty()

        val myId = sessionStore.myUserId()

        dtos
            .filter { clearedAt.isNullOrEmpty() || clearedAt.startsWith("0001") || it.createdAt > clearedAt }
            .map { dto ->
                val role = dto.senderRole?.uppercase()
                // Seller's own messages are those where senderId == myId or role is not BUYER/USER
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
            val odt = OffsetDateTime.parse(isoString)
            val local = odt.atZoneSameInstant(ZoneId.systemDefault())
            local.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            try {
                val parts = isoString.split("T")
                if (parts.size < 2) return isoString
                parts[1].take(5)
            } catch (e2: Exception) {
                isoString ?: ""
            }
        }
    }
}
