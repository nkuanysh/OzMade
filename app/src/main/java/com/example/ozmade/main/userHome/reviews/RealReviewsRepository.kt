package com.example.ozmade.main.userHome.reviews

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.ReviewItemDto
import javax.inject.Inject

class RealReviewsRepository @Inject constructor(
    private val api: OzMadeApi
) : ReviewsRepository {

    override suspend fun getReviews(productId: Int): ReviewsResponse {
        val resp = api.getProductReviews(productId)
        if (!resp.isSuccessful) error("Не удалось загрузить отзывы (${resp.code()})")
        
        val dto = resp.body() ?: error("Пустой ответ от сервера")

        val summary = ReviewsSummaryUi(
            productId = dto.summary?.productId ?: productId,
            averageRating = dto.summary?.averageRating ?: 0.0,
            ratingsCount = dto.summary?.ratingsCount ?: 0,
            reviewsCount = dto.summary?.reviewsCount ?: 0
        )

        val reviews = dto.reviews?.map { r: ReviewItemDto ->
            ReviewUi(
                id = r.id,
                userName = r.userName ?: r.user?.name ?: "Пользователь",
                rating = r.rating ?: 0.0,
                dateText = r.createdAt ?: "",
                text = r.text ?: "",
                photoUrl = com.example.ozmade.utils.ImageUtils.formatProfilePhotoUrl(r.photoUrl ?: r.user?.photoUrl)
            )
        } ?: emptyList()

        return ReviewsResponse(summary = summary, reviews = reviews)
    }
}
