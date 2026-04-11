package com.example.ozmade.main.seller.chat.data

data class SellerChatThreadUi(
    val chatId: Int,
    val buyerId: Int,
    val buyerName: String,
    val buyerPhotoUrl: String? = null,
    val productId: Int = 0,
    val productTitle: String = "",
    val productImageUrl: String? = null,
    val lastMessage: String,
    val lastTimeText: String
)

data class SellerChatMessageUi(
    val id: Int,
    val text: String,
    val isMine: Boolean,
    val timeText: String
)