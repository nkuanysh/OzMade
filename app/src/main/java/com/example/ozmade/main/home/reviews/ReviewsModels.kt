package com.example.ozmade.main.home.reviews

data class ReviewUi(
    val id: String,
    val userName: String,
    val rating: Double, // 0..5 (может быть 3.5)
    val dateText: String, // пока текстом: "15.02.2026"
    val text: String
)

data class ReviewsSummaryUi(
    val productId: String,
    val averageRating: Double,
    val ratingsCount: Int,
    val reviewsCount: Int
)

sealed class ReviewsUiState {
    data object Loading : ReviewsUiState()
    data class Data(
        val titleCount: Int,
        val summary: ReviewsSummaryUi,
        val reviews: List<ReviewUi>
    ) : ReviewsUiState()

    data class Error(val message: String) : ReviewsUiState()
}
