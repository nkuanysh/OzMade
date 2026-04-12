package com.example.ozmade.main.user.chat.data

import android.util.Log
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.OffsetDateTime
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.auth.SessionStore
import com.example.ozmade.network.model.ChatSendMessageRequest
import com.example.ozmade.network.model.CreateChatRequest
import com.example.ozmade.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealChatRepository @Inject constructor(
    private val api: OzMadeApi,
    private val sessionStore: SessionStore
) : ChatRepository {

    private val TAG = "RealChatRepository"

    override suspend fun getThreads(): List<ChatThreadUi> = withContext(Dispatchers.IO) {
        val resp = api.getBuyerChats()
        if (!resp.isSuccessful) error("Не удалось загрузить чаты (${resp.code()})")

        val myId = sessionStore.myUserId()
        val chats = resp.body().orEmpty()

        Log.d(TAG, "getThreads: myId=$myId, received ${chats.size} chats")

        // Filter: for buyer view, we only show chats where we are the buyer and not deleted.
        // We still exclude self-chats from the buyer list if they are intended to only show in the seller list.
        val filtered = chats.filter { chat ->
            val isMyBuyerChat = if (myId != null && myId > 0) chat.buyerId == myId else true
            val isSelfChat = myId != null && chat.sellerId == myId
            isMyBuyerChat && !isSelfChat && !chat.deletedByBuyer
        }

        filtered.map { c ->
            val visibleMessages = c.messages.orEmpty().filter { msg ->
                c.buyerClearedAt.isNullOrEmpty() || c.buyerClearedAt.startsWith("0001") || msg.createdAt > c.buyerClearedAt
            }

            val lastMsgFromVisible = visibleMessages.maxByOrNull { it.id }
            
            // Check if the chat-level last message is also "visible" (newer than clearedAt)
            val isLastMessageVisible = c.lastMessage != null && 
                (c.buyerClearedAt.isNullOrEmpty() || 
                 c.buyerClearedAt.startsWith("0001") || 
                 c.lastMessage.createdAt > c.buyerClearedAt)

            val finalLastMessage = when {
                lastMsgFromVisible != null -> lastMsgFromVisible.content
                isLastMessageVisible -> c.lastMessage?.content ?: c.lastMessageContent ?: "Напишите сообщение..."
                else -> "Напишите сообщение..."
            }

            val timeSource = when {
                lastMsgFromVisible != null -> lastMsgFromVisible.createdAt
                isLastMessageVisible -> c.lastMessage?.createdAt ?: c.updatedAt ?: c.createdAt
                else -> c.updatedAt ?: c.createdAt
            }

            val displayName = c.sellerName ?: c.seller?.name ?: "Продавец #${c.sellerId}"

            ChatThreadUi(
                chatId = c.id,
                sellerId = c.sellerId,
                sellerName = displayName,
                productId = c.productId ?: 0,
                productTitle = c.productName ?: "Без названия",
                productPrice = c.productPrice ?: 0,
                productImageUrl = ImageUtils.formatImageUrl(c.productImage),
                sellerPhotoUrl = ImageUtils.formatImageUrl(c.sellerPhoto),
                lastMessage = finalLastMessage,
                lastTimeText = if (lastMsgFromVisible != null || isLastMessageVisible) formatTime(timeSource) else "",
                sellerNumber = c.phoneNumber as String?,
                isOnline = false
            )
        }.sortedByDescending { it.chatId } // Sorting by ID as a fallback, but server usually sorts by updated_at
    }

    override suspend fun getMessages(chatId: Int): List<ChatMessageUi> = withContext(Dispatchers.IO) {
        val chatsResp = api.getBuyerChats()
        val chat = chatsResp.body()?.find { it.id == chatId }
        val clearedAt = chat?.buyerClearedAt

        val resp = api.getBuyerChatMessages(chatId)
        if (!resp.isSuccessful) error("Не удалось загрузить сообщения (${resp.code()})")

        val myId = sessionStore.myUserId()
        val messages = resp.body().orEmpty()
        
        Log.d(TAG, "getMessages: chatId=$chatId, myId=$myId, total messages=${messages.size}, clearedAt=$clearedAt")

        messages
            .filter { clearedAt.isNullOrEmpty() || clearedAt.startsWith("0001") || it.createdAt > clearedAt }
            .map { dto ->
                val role = dto.senderRole?.uppercase()
                val isMine = if (myId != null && myId > 0) {
                    dto.senderId == myId
                } else {
                    role == "BUYER" || role == "USER"
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
