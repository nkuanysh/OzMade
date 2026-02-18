package com.example.ozmade.network.dto

data class SellerHeaderDto(
    val id: String,
    val name: String,
    val status: String,
    val ordersCount: Int,
    val rating: Double,
    val reviewsCount: Int,
    val daysWithOzMade: Int
)

data class SellerProductDto(
    val id: String,
    val title: String,
    val price: Int,
    val city: String,
    val address: String,
    val rating: Double
)

data class SellerPageDto(
    val seller: SellerHeaderDto,
    val products: List<SellerProductDto>
)
