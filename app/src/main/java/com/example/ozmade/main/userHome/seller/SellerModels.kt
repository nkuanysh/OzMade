package com.example.ozmade.main.userHome.seller

data class SellerHeaderUi(
    val id: Int,
    val name: String,
    val status: String,         // "Новый мастер" и т.д.
    val ordersCount: Int,
    val rating: Double,
    val reviewsCount: Int,
    val daysWithOzMade: Int
)

data class SellerProductUi(
    val id: Int,
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
        val likedIds: Set<Int>
    ) : SellerUiState()
    data class Error(val message: String) : SellerUiState()
}
