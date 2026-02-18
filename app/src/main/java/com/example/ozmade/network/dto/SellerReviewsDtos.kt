package com.example.ozmade.network.dto

data class SellerReviewsHeaderDto(
    val sellerId: String,
    val sellerName: String,
    val reviewsCount: Int,
    val averageRating: Double,
    val ratingsCount: Int
)

data class SellerReviewDto(
    val id: String,
    val userName: String,
    val productId: String,
    val productTitle: String,
    val rating: Double,
    val createdAt: String,
    val text: String
)

data class SellerReviewsResponseDto(
    val header: SellerReviewsHeaderDto,
    val reviews: List<SellerReviewDto>
)
