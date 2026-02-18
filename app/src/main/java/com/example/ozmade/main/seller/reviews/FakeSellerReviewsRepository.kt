package com.example.ozmade.main.seller.reviews

import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeSellerReviewsRepository @Inject constructor() : SellerReviewsRepository {

    override suspend fun getSellerReviews(sellerId: String): SellerReviewsResponse {
        delay(450)

        val sellerName = if (sellerId == "seller_1") "Айгерим" else "Продавец"

        val reviews = listOf(
            SellerReviewUi(
                id = "sr1",
                userName = "Алия",
                productId = "1",
                productTitle = "Домашний сыр",
                rating = 5.0,
                dateText = "15.02.2026",
                text = "Очень вкусно, свежий продукт!"
            ),
            SellerReviewUi(
                id = "sr2",
                userName = "Данияр",
                productId = "8",
                productTitle = "Букет к 8 марта (очень красивый, большой и яркий)",
                rating = 4.5,
                dateText = "11.02.2026",
                text = "Букет классный, но хотелось бы упаковку плотнее."
            ),
            SellerReviewUi(
                id = "sr3",
                userName = "Руслан",
                productId = "2",
                productTitle = "Тойбастар набор",
                rating = 4.0,
                dateText = "02.02.2026",
                text = "В целом отлично, пришло вовремя."
            )
        )

        val avg = reviews.map { it.rating }.average()

        val header = SellerReviewsHeaderUi(
            sellerId = sellerId,
            sellerName = sellerName,
            reviewsCount = reviews.size,
            averageRating = avg,
            ratingsCount = reviews.size
        )

        return SellerReviewsResponse(header, reviews)
    }
}
