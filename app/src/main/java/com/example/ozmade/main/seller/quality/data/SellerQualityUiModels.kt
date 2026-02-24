package com.example.ozmade.main.seller.quality.data

data class SellerQualityUi(
    val sellerName: String,
    val levelTitle: String,
    val levelProgress: Float, // 0..1
    val levelHint: String,

    val averageRating: Double,
    val ratingsCount: Int,

    val reviewsCount: Int,
    val reviews: List<SellerQualityReviewUi>
)

data class SellerQualityReviewUi(
    val id: String,
    val userName: String,
    val productId: String,
    val productTitle: String,
    val rating: Double,
    val dateText: String,
    val text: String
)

sealed class SellerQualityUiState {
    data object Loading : SellerQualityUiState()
    data class Error(val message: String) : SellerQualityUiState()
    data class Data(val data: SellerQualityUi) : SellerQualityUiState()
}