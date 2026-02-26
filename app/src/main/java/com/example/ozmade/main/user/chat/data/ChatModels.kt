package com.example.ozmade.main.user.chat.data

data class ChatThreadUi(
    val chatId: Int,
    val sellerId: Int,
    val sellerName: String,      // если нет — можно "Продавец #id"
    val productId: Int,
    val productTitle: String,
    val productPrice: Int,
    val productImageUrl: String? = null,
    val lastMessage: String,
    val lastTimeText: String
)

data class ChatMessageUi(
    val id: String,
    val text: String,
    val isMine: Boolean,
    val timeText: String
)
