package com.example.ozmade.main.userHome.seller.reviews

import com.example.ozmade.network.api.OzMadeApi
import javax.inject.Inject

class RealSellerReviewsRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerReviewsRepository {

    override suspend fun getSellerReviews(sellerId: Int): SellerReviewsResponse {
        val resp = api.getSellerReviews(sellerId)
        if (!resp.isSuccessful) error("Не удалось загрузить отзывы продавца (${resp.code()})")
        
        val dto = resp.body() ?: error("Пустой ответ от сервера")

        val header = SellerReviewsHeaderUi(
            sellerId = dto.header?.sellerId ?: sellerId,
            sellerName = dto.header?.sellerName ?: "Продавец",
            reviewsCount = dto.header?.reviewsCount ?: 0,
            averageRating = dto.header?.averageRating ?: 0.0,
            ratingsCount = dto.header?.ratingsCount ?: 0
        )

        val reviews = dto.reviews?.map {
            SellerReviewUi(
                id = it.id,
                userName = it.userName,
                productId = it.productId,
                productTitle = it.productTitle,
                rating = it.rating,
                dateText = it.createdAt,
                text = it.text
            )
        } ?: emptyList()

        return SellerReviewsResponse(header, reviews)
    }
}
