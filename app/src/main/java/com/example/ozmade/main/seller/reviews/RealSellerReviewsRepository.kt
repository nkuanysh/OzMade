package com.example.ozmade.main.seller.reviews

import com.example.ozmade.network.api.SellerReviewsApi
import javax.inject.Inject

class RealSellerReviewsRepository @Inject constructor(
    private val api: SellerReviewsApi
) : SellerReviewsRepository {

    override suspend fun getSellerReviews(sellerId: String): SellerReviewsResponse {
        val dto = api.getSellerReviews(sellerId)

        val header = SellerReviewsHeaderUi(
            sellerId = dto.header.sellerId,
            sellerName = dto.header.sellerName,
            reviewsCount = dto.header.reviewsCount,
            averageRating = dto.header.averageRating,
            ratingsCount = dto.header.ratingsCount
        )

        val reviews = dto.reviews.map {
            SellerReviewUi(
                id = it.id,
                userName = it.userName,
                productId = it.productId,
                productTitle = it.productTitle,
                rating = it.rating,
                dateText = it.createdAt,
                text = it.text
            )
        }

        return SellerReviewsResponse(header, reviews)
    }
}
