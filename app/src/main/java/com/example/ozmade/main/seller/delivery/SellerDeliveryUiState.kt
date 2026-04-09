package com.example.ozmade.main.seller.delivery

data class SellerDeliveryUi(
    val pickupEnabled: Boolean,
    val pickupAddress: String,
    val pickupTime: String,

    val myDeliveryEnabled: Boolean,
    val centerLat: String,      // в UI строкой (чтобы вводить)
    val centerLng: String,
    val radiusKm: Int,
    val centerAddress: String,

    val intercityEnabled: Boolean
)

sealed class SellerDeliveryUiState {
    data object Loading : SellerDeliveryUiState()
    data class Error(val message: String) : SellerDeliveryUiState()
    data class Data(val ui: SellerDeliveryUi) : SellerDeliveryUiState()
}