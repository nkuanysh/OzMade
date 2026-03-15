package com.example.ozmade.main.userHome.seller.reviews

interface SellerReviewsRepository {
    suspend fun getSellerReviews(sellerId: Int): SellerReviewsResponse
}

data class SellerReviewsResponse(
    val header: SellerReviewsHeaderUi,
    val reviews: List<SellerReviewUi>
)
