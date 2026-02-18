package com.example.ozmade.main.reviews

interface ReviewsRepository {
    suspend fun getReviews(productId: String): ReviewsResponse
}

data class ReviewsResponse(
    val summary: ReviewsSummaryUi,
    val reviews: List<ReviewUi>
)
