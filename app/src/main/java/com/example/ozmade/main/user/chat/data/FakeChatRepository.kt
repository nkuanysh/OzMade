package com.example.ozmade.main.user.chat.data

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeChatRepository @Inject constructor() : ChatRepository {

    private val threads = LinkedHashMap<String, ChatThreadUi>()
    private val messages = LinkedHashMap<String, MutableList<ChatMessageUi>>()

    override suspend fun getThreads(): List<ChatThreadUi> {
        delay(150)
        // самые новые сверху
        return threads.values.toList().reversed()
    }

    override suspend fun getMessages(threadId: String): List<ChatMessageUi> {
        delay(120)
        return messages[threadId]?.toList().orEmpty()
    }

    override suspend fun ensureThread(
        sellerId: String,
        sellerName: String,
        productId: String,
        productTitle: String,
        productPrice: Int,
        productImageUrl: String?
    ): String {
        val threadId = "$sellerId:$productId"
        if (!threads.containsKey(threadId)) {
            threads[threadId] = ChatThreadUi(
                threadId = threadId,
                sellerId = sellerId,
                sellerName = sellerName,
                productId = productId,
                productTitle = productTitle,
                productPrice = productPrice,
                productImageUrl = productImageUrl,
                lastMessage = "Напишите сообщение…",
                lastTimeText = ""
            )
            messages[threadId] = mutableListOf()
        }
        return threadId
    }

    override suspend fun sendMessage(threadId: String, text: String) {
        delay(120)
        val time = nowTime()
        val list = messages.getOrPut(threadId) { mutableListOf() }
        list.add(
            ChatMessageUi(
                id = "${threadId}_${list.size + 1}",
                text = text,
                isMine = true,
                timeText = time
            )
        )

        // обновляем тред
        val old = threads[threadId] ?: return
        threads[threadId] = old.copy(
            lastMessage = text,
            lastTimeText = time
        )

        // имитация ответа продавца (чисто чтобы видно было)
        if (text.length > 2) {
            list.add(
                ChatMessageUi(
                    id = "${threadId}_${list.size + 1}",
                    text = "Ок, понял! Сейчас уточню 👍",
                    isMine = false,
                    timeText = nowTime()
                )
            )
            threads[threadId] = threads[threadId]!!.copy(
                lastMessage = "Ок, понял! Сейчас уточню 👍",
                lastTimeText = nowTime()
            )
        }
    }

    private fun nowTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
