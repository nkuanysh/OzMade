//package com.example.ozmade.main.user.chat.data
//
//import com.example.ozmade.network.api.OzMadeApi
//import com.example.ozmade.network.model.EnsureThreadRequest
//import com.example.ozmade.network.model.SendMessageRequest
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class FakeChatRepository @Inject constructor(
//    private val api: OzMadeApi
//) : ChatRepository {
//
//    override suspend fun getThreads(): List<ChatThreadUi> = withContext(Dispatchers.IO) {
//        try {
//            val response = api.getBuyerChats()
//            val dtos = response.body() ?: emptyList()
//
//            dtos.map { dto ->
//                ChatThreadUi(
//                    threadId = dto.threadId,
//                    sellerId = dto.sellerId,
//                    sellerName = dto.sellerName,
//                    productId = dto.productId,
//                    productTitle = dto.productTitle,
//                    productPrice = dto.productPrice,
//                    productImageUrl = dto.productImageUrl,
//                    lastMessage = dto.lastMessage,
//                    lastTimeText = dto.lastTimeText
//                )
//            }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
//
//    override suspend fun getMessages(threadId: String): List<ChatMessageUi> = withContext(Dispatchers.IO) {
//        try {
//            val response = api.getBuyerChatMessages(threadId)
//            val dtos = response.body() ?: emptyList()
//
//            dtos.map { dto ->
//                ChatMessageUi(
//                    id = dto.id,
//                    text = dto.text,
//                    isMine = dto.isMine,
//                    timeText = dto.timeText
//                )
//            }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
//
//    override suspend fun ensureThread(
//        sellerId: String,
//        sellerName: String,
//        productId: String,
//        productTitle: String,
//        productPrice: Int,
//        productImageUrl: String?
//    ): String = withContext(Dispatchers.IO) {
//        try {
//            val request = EnsureThreadRequest(sellerId, productId)
//            val response = api.ensureBuyerChat(request)
//
//            // Return the thread ID from the backend, or fallback to a local combined ID if it fails
//            response.body()?.threadId ?: "${sellerId}:${productId}"
//        } catch (e: Exception) {
//            "${sellerId}:${productId}"
//        }
//    }
//
//    override suspend fun sendMessage(threadId: String, text: String): Unit = withContext(Dispatchers.IO) {
//        try {
//            val request = SendMessageRequest(text)
//            api.sendBuyerChatMessage(threadId, request)
//        } catch (e: Exception) {
//            // Handle error (e.g., store locally to retry later, or throw to UI)
//            e.printStackTrace()
//        }
//    }
//}