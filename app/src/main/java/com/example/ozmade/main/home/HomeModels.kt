package com.example.ozmade.main.home

data class AdBanner(
    val id: String,
    val imageUrl: String? = null,
    val title: String? = null,
    val deeplink: String? = null
)

data class Category(
    val id: String,
    val title: String,
    val iconUrl: String? = null
)

data class Product(
    val id: String,
    val title: String,
    val price: Int,
    val city: String,
    val address: String,
    val rating: Double,
    val imageUrl: String? = null,
    val categoryId: String
)

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Data(
        val ads: List<AdBanner>,
        val categories: List<Category>,
        val products: List<Product>
    ) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
}
