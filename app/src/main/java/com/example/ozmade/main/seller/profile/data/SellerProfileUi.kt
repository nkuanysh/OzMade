package com.example.ozmade.main.seller.profile.data

import com.example.ozmade.main.orders.data.OrderUi

data class SellerProfileUi(
    val name: String,
    val firstName: String?,
    val lastName: String?,
    val about: String?,
    val city: String?,
    val address: String?,
    val categories: List<String>?,
    val levelTitle: String?,
    val levelProgress: Float?,
    val levelHint: String?,
    val photoUrl: String?,
    val totalProducts: Int,
    val rating: Double,
    val ratingsCount: Int,
    val ordersCount: Int,
    val activeOrders: List<OrderUi> = emptyList()
)