package com.example.ozmade.main.userHome.reviews

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.ReviewItemDto
import javax.inject.Inject

class RealReviewsRepository @Inject constructor(
    private val api: OzMadeApi
) : ReviewsRepository {

    override suspend fun getReviews(productId: Int): ReviewsResponse {
        val dto = api.getProductReviews(productId)

        val summary = ReviewsSummaryUi(
            productId = dto.summary.productId,
            averageRating = dto.summary.averageRating,
            ratingsCount = dto.summary.ratingsCount,
            reviewsCount = dto.summary.reviewsCount
        )

        val reviews = dto.reviews.map { r: ReviewItemDto ->
            ReviewUi(
                id = r.id,
                userName = r.userName,
                rating = r.rating,
                dateText = r.createdAt, // пока просто показываем как текст
                text = r.text
            )
        }

        return ReviewsResponse(summary = summary, reviews = reviews)
    }
}
