package com.example.ozmade.main.userHome.details

import com.example.ozmade.main.userHome.reviews.ReviewUi
import com.example.ozmade.main.delivery.IntercityDeliveryEstimate

data class ProductPackageUi(
    val weightGrams: Int,
    val heightCm: Int,
    val widthCm: Int,
    val depthCm: Int
)

data class DeliveryInfoUi(
    val pickupEnabled: Boolean = false,
    val pickupTime: String? = null,
    val freeDeliveryEnabled: Boolean = false,
    val freeDeliveryText: String? = null,
    val intercityEnabled: Boolean = false,
    val pickupAddress: String? = null,
    val pickupLat: Double? = null,
    val pickupLng: Double? = null,
    val centerAddress: String? = null,
    val centerLat: Double? = null,
    val centerLng: Double? = null,
    val radiusKm: Double? = null,

    val buyerSavedAddress: String? = null,
    val buyerSavedAddressLat: Double? = null,
    val buyerSavedAddressLng: Double? = null,
    val buyerSavedCity: String? = null,
    val sellerPickupCity: String? = null,

    val isBuyerInsideDeliveryZone: Boolean? = null

)
data class SellerUi(
    val id: Int,
    val name: String,
    val photoUrl: String? = null,
    val address: String,
    val rating: Double,
    val completedOrders: Int
)

data class ProductDetailsUi(
    val id: Int,
    val title: String,
    val price: Double,
    val rating: Double,
    val reviewsCount: Int,
    val ordersCount: Int,
    val images: List<String>,
    val youtubeUrl: String? = null,
    val description: String,
    val specs: List<Pair<String, String>>,
    val packageInfo: ProductPackageUi,
    val delivery: DeliveryInfoUi,
    val seller: SellerUi,
    val reviews: List<ReviewUi> = emptyList(),
    val isMine: Boolean = false
)

sealed class ProductIntercityEstimateUiState {
    data object Disabled : ProductIntercityEstimateUiState()
    data object Loading : ProductIntercityEstimateUiState()
    data object MissingBuyerAddress : ProductIntercityEstimateUiState()
    data object MissingSellerAddress : ProductIntercityEstimateUiState()
    data object SameCity : ProductIntercityEstimateUiState()
    data class Success(
        val estimate: IntercityDeliveryEstimate,
        val fromCity: String,
        val toCity: String
    ) : ProductIntercityEstimateUiState()
    data class Error(val message: String) : ProductIntercityEstimateUiState()
}

sealed class ProductDetailsUiState {
    data object Loading : ProductDetailsUiState()
    data class Data(
        val product: ProductDetailsUi,
        val liked: Boolean,
        val intercityEstimate: ProductIntercityEstimateUiState = ProductIntercityEstimateUiState.Disabled
    ) : ProductDetailsUiState()
    data class Error(val message: String) : ProductDetailsUiState()
}
