package com.example.ozmade.main.userHome.details

data class DeliveryInfoUi(
    val pickupEnabled: Boolean = false,
    val pickupTime: String? = null, // "12:00-18:00"
    val freeDeliveryEnabled: Boolean = false,
    val freeDeliveryText: String? = null,
    val intercityEnabled: Boolean = false,
)
data class SellerUi(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val address: String,
    val rating: Double,
    val completedOrders: Int
)

data class ProductDetailsUi(
    val id: String,
    val title: String,
    val price: Double,
    val rating: Double,
    val reviewsCount: Int,
    val ordersCount: Int,
    val images: List<String>,
    val description: String,
    val specs: List<Pair<String, String>>,
    val delivery: DeliveryInfoUi,
    val seller: SellerUi

)

sealed class ProductDetailsUiState {
    data object Loading : ProductDetailsUiState()
    data class Data(val product: ProductDetailsUi, val liked: Boolean) : ProductDetailsUiState()
    data class Error(val message: String) : ProductDetailsUiState()
}
