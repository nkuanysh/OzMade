package com.example.ozmade.network.dto

data class ReviewsSummaryDto(
    val productId: String,
    val averageRating: Double,
    val ratingsCount: Int,
    val reviewsCount: Int
)

data class ReviewDto(
    val id: String,
    val userName: String,
    val rating: Double,     // может быть 3.5
    val createdAt: String,  // лучше ISO: "2026-02-15T10:20:00Z" или уже "15.02.2026"
    val text: String
)

data class ReviewsResponseDto(
    val summary: ReviewsSummaryDto,
    val reviews: List<ReviewDto>
)
