package com.example.ozmade.main.userHome.seller.reviews

data class SellerReviewsHeaderUi(
    val sellerId: Int,
    val sellerName: String,
    val reviewsCount: Int,
    val averageRating: Double,
    val ratingsCount: Int
)

data class SellerReviewUi(
    val id: Int,
    val userName: String,
    val productId: Int,
    val productTitle: String,
    val rating: Double,
    val dateText: String,
    val text: String
)

sealed class SellerReviewsUiState {
    data object Loading : SellerReviewsUiState()
    data class Data(
        val header: SellerReviewsHeaderUi,
        val reviews: List<SellerReviewUi>
    ) : SellerReviewsUiState()
    data class Error(val message: String) : SellerReviewsUiState()
}
