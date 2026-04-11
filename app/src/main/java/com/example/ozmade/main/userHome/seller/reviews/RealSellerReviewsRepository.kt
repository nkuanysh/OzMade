package com.example.ozmade.main.userHome.seller.reviews

import com.example.ozmade.network.api.OzMadeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealSellerReviewsRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerReviewsRepository {

    override suspend fun getSellerReviews(sellerId: Int): SellerReviewsResponse = withContext(Dispatchers.IO) {
        val resp = api.getSellerReviewLegacy(sellerId)
        if (!resp.isSuccessful) error("Ошибка загрузки отзывов: ${resp.code()}")

        val dto = resp.body() ?: error("Пустой ответ")

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
                userName = it.userName ?: it.user?.name ?: "Пользователь",
                productId = it.productId ?: 0,
                productTitle = it.productTitle ?: "Товар",
                rating = it.rating ?: 0.0,
                dateText = it.createdAt ?: "",
                text = it.text ?: ""
            )
        } ?: emptyList()

        return@withContext SellerReviewsResponse(header, reviews)
    }
}
