package com.example.ozmade.main.seller.profile.data

data class SellerProfileUi(
    val name: String,
    val firstName: String?,
    val lastName: String?,
    val about: String?,
    val city: String?,
    val address: String?,
    val categories: List<String>?,
    val status: String,
    val avatarUrl: String?,
    val totalProducts: Int,
    val rating: Double,
    val ratingsCount: Int,
    val ordersCount: Int
)