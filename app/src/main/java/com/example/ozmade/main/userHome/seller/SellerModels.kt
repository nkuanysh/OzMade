package com.example.ozmade.main.userHome.seller

data class SellerHeaderUi(
    val id: Int,
    val name: String,
    val storeName: String? = null,
    val status: String,         // "Новый мастер" и т.д.
    val ordersCount: Int,
    val rating: Double,
    val reviewsCount: Int,
    val daysWithOzMade: Int,
    val avatarUrl: String? = null,
    val city: String? = null,
    val description: String? = null,
    val categories: String? = null,
    val levelTitle: String? = null,
    val levelProgress: Float? = null,
    val levelHint: String? = null
)

data class SellerProductUi(
    val id: Int,
    val title: String,
    val price: Double,
    val city: String,
    val address: String,
    val rating: Double,
    val imageUrl: String? = null
)

sealed class SellerUiState {
    data object Loading : SellerUiState()
    data class Data(
        val seller: SellerHeaderUi,
        val products: List<SellerProductUi>,
        val likedIds: Set<Int>
    ) : SellerUiState()
    data class Error(val message: String) : SellerUiState()
}
