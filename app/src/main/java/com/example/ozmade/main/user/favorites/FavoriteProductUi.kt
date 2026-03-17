package com.example.ozmade.main.user.favorites

data class FavoriteProductUi(
    val id: Int,
    val title: String,
    val price: Double,
    val imageUrl: String?,
    val address: String,
    val rating: Double
)