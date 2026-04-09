package com.example.ozmade.main.seller.profile.data

data class SellerProfileUi(
    val name: String,
    val status: String,
    val totalProducts: Int,
    val rating: Double,
    val ratingsCount: Int,
    val ordersCount: Int
)