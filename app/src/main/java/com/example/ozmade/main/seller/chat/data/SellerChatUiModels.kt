package com.example.ozmade.main.seller.chat.data

data class SellerChatThreadUi(
    val chatId: Int,
    val buyerId: Int,
    val buyerName: String,      // пока заглушка (см. ниже)
    val lastMessage: String,
    val lastTimeText: String
)

data class SellerChatMessageUi(
    val id: String,
    val text: String,
    val isMine: Boolean,
    val timeText: String
)