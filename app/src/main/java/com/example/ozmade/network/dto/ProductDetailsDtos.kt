package com.example.ozmade.network.dto

data class SellerDto(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val address: String,
    val rating: Double,
    val completedOrders: Int
)

data class DeliveryDto(
    val pickupEnabled: Boolean,
    val pickupTime: String?,
    val freeDeliveryEnabled: Boolean,
    val freeDeliveryText: String?,
    val intercityEnabled: Boolean
)

data class ProductDetailsDto(
    val id: String,
    val title: String,
    val price: Int,
    val images: List<String>,
    val description: String,
    val specs: Map<String, String>,
    val rating: Double,
    val reviewsCount: Int,
    val ordersCount: Int,
    val delivery: DeliveryDto,
    val seller: SellerDto
)
