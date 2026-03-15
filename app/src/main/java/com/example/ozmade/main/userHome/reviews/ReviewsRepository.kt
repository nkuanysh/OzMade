package com.example.ozmade.main.userHome.reviews

interface ReviewsRepository {
    suspend fun getReviews(productId: Int): ReviewsResponse
}

data class ReviewsResponse(
    val summary: ReviewsSummaryUi,
    val reviews: List<ReviewUi>
)
