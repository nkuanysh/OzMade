package com.example.ozmade.main.home

data class AdBanner(
    val id: String,
    val title: String,
    val deeplink: String? = null,
    val imageRes: Int? = null  // ресурс картинки, например R.drawable.banner1
)

data class Category(
    val id: String,
    val title: String
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
    val id: String,
    val title: String,
    val price: Int,
    val city: String,
    val address: String,
    val rating: Double,
    val categoryId: String,
    val imageUrl: String? = null
)

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Data(
        val ads: List<AdBanner> = emptyList(),
        val categories: List<Category>,
        val products: List<Product>
    ) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
}
