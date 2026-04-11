package com.example.ozmade.main.userHome

data class AdBanner(
    val id: String,
    val title: String,
    val imageUrl: String? = null,
    val imageRes: Int? = null,
    val deeplink: String? = null
)

data class Category(
    val id: String,
    val title: String,
    val iconUrl: String? = null
)

val categories = listOf(
    Category("food", "Еда"),
    Category("clothes", "Одежда"),
    Category("art", "Искусство"),
    Category("craft", "Ремесло"),
    Category("gifts", "Подарки"),
    Category("holiday", "Праздники"),
    Category("home", "Для дома")
)

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val imageUrl: String? = null,
    val city: String = "",
    val address: String = "",
    val rating: Double = 4.5,
    val categoryId: String = "",
    val liked: Boolean = false
)

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Data(
        val products: List<Product>,
        val recommendations: List<Product> = emptyList(),
        val categories: List<Category>,
        val ads: List<AdBanner>,
        val searchQuery: String = ""
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
