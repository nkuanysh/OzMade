package com.example.ozmade.main.seller.quality.data

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.SellerReviewItemDto
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
        // Try to load dedicated quality stats first
        val qualityResp = runCatching { api.getSellerQuality() }.getOrNull()
        if (qualityResp != null && qualityResp.isSuccessful) {
            val dto = qualityResp.body()!!
            val level = computeLevel(
                orders = dto.ordersCount ?: 0,
                rating = dto.averageRating ?: 0.0,
                reviews = dto.reviews_count ?: 0,
                days = dto.daysWithOzMade ?: 0
            )
            return@withContext SellerQualityUi(
                sellerName = dto.sellerName ?: "",
                levelTitle = level.title,
                levelProgress = level.progress,
                levelHint = level.hint,
                averageRating = dto.averageRating ?: 0.0,
                ratingsCount = dto.ratingsCount ?: 0,
                reviewsCount = dto.reviews_count ?: 0,
                reviews = dto.reviews?.map { r -> mapToUi(r) } ?: emptyList()
            )
        }

        // Fallback: 1. Get profile to find the seller ID
        val profileResp = api.getSellerProfile()
        if (!profileResp.isSuccessful) error("Не удалось загрузить профиль (${profileResp.code()})")
        val profile = profileResp.body() ?: error("Пустой ответ профиля")

        // 2. Load reviews using the ID (using the suggested endpoint seller/:id/review)
        val reviewsResp = api.getSellerReviewLegacy(profile.id)
        val reviewsDto = if (reviewsResp.isSuccessful) reviewsResp.body() else null
        val header = reviewsDto?.header

        // 3. Compute stats
        val ordersCount = profile.ordersCount ?: 0
        val averageRating = header?.averageRating ?: profile.rating ?: 0.0
        val reviewsCount = header?.reviewsCount ?: dtoReviewsCount(reviewsDto?.reviews) ?: 0
        val ratingsCount = header?.ratingsCount ?: reviewsCount
        val daysWithOzMade = profile.daysWithOzMade ?: 0

        val level = computeLevel(
            orders = ordersCount,
            rating = averageRating,
            reviews = reviewsCount,
            days = daysWithOzMade
        )

        SellerQualityUi(
            sellerName = profile.name,
            levelTitle = level.title,
            levelProgress = level.progress,
            levelHint = level.hint,

            averageRating = averageRating,
            ratingsCount = ratingsCount,

            reviewsCount = reviewsCount,
            reviews = reviewsDto?.reviews?.map { r -> mapToUi(r) } ?: emptyList()
        )
    }

    private fun mapToUi(r: SellerReviewItemDto) = SellerQualityReviewUi(
        id = r.id,
        userName = r.userName ?: r.user?.name ?: "Пользователь",
        productId = r.productId ?: 0,
        productTitle = r.productTitle ?: "Товар",
        rating = r.rating ?: 0.0,
        dateText = r.createdAt ?: "",
        text = r.text ?: ""
    )

    private fun dtoReviewsCount(list: List<SellerReviewItemDto>?): Int? {
        return list?.size?.takeIf { it > 0 }
    }

    private data class Level(val title: String, val progress: Float, val hint: String)

    private fun computeLevel(orders: Int, rating: Double, reviews: Int, days: Int): Level {
        val ordersPts = min(40, orders * 2)
        val reviewsPts = min(30, reviews * 3)
        val ratingRaw = max(0.0, rating - 3.0) * 10.0
        val ratingPts = min(20, ratingRaw.toInt())
        val daysPts = min(10, days / 7)

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
