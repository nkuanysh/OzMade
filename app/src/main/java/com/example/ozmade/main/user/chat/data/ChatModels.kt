package com.example.ozmade.main.user.chat.data

data class ChatThreadUi(
    val chatId: Int,
    val sellerNumber: String?,
    val sellerId: Int,
    val sellerName: String,
    val productId: Int,
    val productTitle: String,
    val productPrice: Int,
    val productImageUrl: String? = null,
    val sellerPhotoUrl: String? = null,
    val lastMessage: String,
    val lastTimeText: String,
    val isOnline: Boolean = false // Добавили реальное поле статуса
)

data class ChatMessageUi(
    val id: Int,
    val text: String,
    val isMine: Boolean,
    val timeText: String
)
