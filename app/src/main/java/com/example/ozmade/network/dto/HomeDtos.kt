package com.example.ozmade.network.dto

data class AdDto(
    val id: String,
    val title: String? = null,
    val imageUrl: String? = null,
    val deeplink: String? = null
)

data class CategoryDto(
    val id: String,
    val title: String,
    val iconUrl: String? = null
)

data class ProductDto(
    val id: String,
    val title: String,
    val price: Int,
    val city: String,
    val address: String,
    val rating: Double,
    val imageUrl: String? = null,
    val categoryId: String
)
