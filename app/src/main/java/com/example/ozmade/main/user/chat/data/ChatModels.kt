package com.example.ozmade.main.user.chat.data

data class ChatThreadUi(
    val threadId: String,      // например: "$sellerId:$productId"
    val sellerId: String,
    val sellerName: String,
    val productId: String,
    val productTitle: String,
    val productPrice: Int,
    val productImageUrl: String? = null, // позже Coil
    val lastMessage: String,
    val lastTimeText: String
)

data class ChatMessageUi(
    val id: String,
    val text: String,
    val isMine: Boolean,
    val timeText: String
)
