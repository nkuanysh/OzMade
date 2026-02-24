package com.example.ozmade.main.seller.quality.data

import com.example.ozmade.network.api.OzMadeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class RealSellerQualityRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerQualityRepository {

    override suspend fun load(): SellerQualityUi = withContext(Dispatchers.IO) {
        val resp = api.getSellerQuality()
        if (!resp.isSuccessful) error("Не удалось загрузить качество (${resp.code()})")
        val dto = resp.body() ?: error("Пустой ответ")

        val level = computeLevel(
            orders = dto.ordersCount,
            rating = dto.averageRating,
            reviews = dto.reviewsCount,
            days = dto.daysWithOzMade
        )

        SellerQualityUi(
            sellerName = dto.sellerName,
            levelTitle = level.title,
            levelProgress = level.progress,
            levelHint = level.hint,

            averageRating = dto.averageRating,
            ratingsCount = dto.ratingsCount,

            reviewsCount = dto.reviewsCount,
            reviews = dto.reviews.map { r ->
                SellerQualityReviewUi(
                    id = r.id,
                    userName = r.userName,
                    productId = r.productId,
                    productTitle = r.productTitle,
                    rating = r.rating,
                    dateText = r.createdAt,   // потом можно красиво форматировать
                    text = r.text
                )
            }
        )
    }

    private data class Level(val title: String, val progress: Float, val hint: String)

    /**
     * Простая модель уровней.
     * Позже можно переносить это на бэк и отдавать level прямо готовым.
     */
    private fun computeLevel(orders: Int, rating: Double, reviews: Int, days: Int): Level {

        val ordersPts = min(40, orders * 2)          // Int
        val reviewsPts = min(30, reviews * 3)        // Int

        // rating: 3.0..5.0 -> 0..20
        val ratingRaw = max(0.0, rating - 3.0) * 10.0   // Double
        val ratingPts = min(20, ratingRaw.toInt())      // Int ✅ теперь min(Int, Int)

        val daysPts = min(10, days / 7)              // Int

        val score = ordersPts + reviewsPts + ratingPts + daysPts

        val s = score.coerceIn(0, 100)
        val progress = s / 100f

        return when {
            s < 20 -> Level("Новый мастер", progress, "Начни собирать отзывы и выполненные заказы")
            s < 45 -> Level("Надёжный мастер", progress, "Держи рейтинг и увеличивай число заказов")
            s < 70 -> Level("Проверенный мастер", progress, "Ещё немного — и ты в топе")
            s < 90 -> Level("Отличный мастер", progress, "Стабильная работа, высокий рейтинг")
            else -> Level("Топ мастер", progress, "Максимальный уровень доверия покупателей")
        }
    }
}