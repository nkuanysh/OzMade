package com.example.ozmade.main.home.seller.reviews

interface SellerReviewsRepository {
    suspend fun getSellerReviews(sellerId: String): SellerReviewsResponse
}

data class SellerReviewsResponse(
    val header: SellerReviewsHeaderUi,
    val reviews: List<SellerReviewUi>
)
