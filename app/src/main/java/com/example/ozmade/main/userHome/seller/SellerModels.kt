package com.example.ozmade.main.userHome.seller

data class SellerHeaderUi(
    val id: String,
    val name: String,
    val status: String,         // "Новый мастер" и т.д.
    val ordersCount: Int,
    val rating: Double,
    val reviewsCount: Int,
    val daysWithOzMade: Int
)

data class SellerProductUi(
    val id: String,
    val title: String,
    val price: Double,
    val city: String,
    val address: String,
    val rating: Double
)

sealed class SellerUiState {
    data object Loading : SellerUiState()
    data class Data(
        val seller: SellerHeaderUi,
        val products: List<SellerProductUi>,
        val likedIds: Set<String>
    ) : SellerUiState()
    data class Error(val message: String) : SellerUiState()
}
