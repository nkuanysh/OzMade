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
        val qualityResp = runCatching { api.getSellerQuality() }.getOrNull()
        
        if (qualityResp != null && qualityResp.isSuccessful) {
            val dto = qualityResp.body()!!
            
            val reviewsCount = dto.reviews_count ?: 0
            val ratingsCount = dto.ratingsCount ?: 0
            
            // LOGIC FIX: If there are no ratings, the average MUST be 0.0
            // Even if the server returns 4.0 or 5.0 as default
            val averageRating = if (ratingsCount > 0) dto.averageRating ?: 0.0 else 0.0

            val level = computeLevel(
                orders = dto.ordersCount ?: 0,
                rating = averageRating,
                reviews = max(reviewsCount, ratingsCount),
                days = dto.daysWithOzMade ?: 0
            )
            return@withContext SellerQualityUi(
                sellerName = dto.sellerName ?: "",
                levelTitle = level.title,
                levelProgress = level.progress,
                levelHint = level.hint,
                averageRating = averageRating,
                ratingsCount = ratingsCount,
                reviewsCount = reviewsCount,
                reviews = dto.reviews?.map { r -> mapToUi(r) } ?: emptyList()
            )
        }

        // Fallback to profile and reviews
        val profileResp = api.getSellerProfile()
        if (!profileResp.isSuccessful) error("Не удалось загрузить профиль (${profileResp.code()})")
        val profile = profileResp.body() ?: error("Пустой ответ профиля")

        val reviewsResp = api.getSellerReviewLegacy(profile.id)
        val reviewsDto = if (reviewsResp.isSuccessful) reviewsResp.body() else null
        val header = reviewsDto?.header

        val ordersCount = profile.ordersCount ?: 0
        val reviewsCountFromList = reviewsDto?.reviews?.size ?: 0
        val reviewsCount = header?.reviewsCount ?: reviewsCountFromList
        val ratingsCount = header?.ratingsCount ?: reviewsCount
        
        // LOGIC FIX: Average rating should be 0 if no ratings exist
        val averageRating = if (ratingsCount > 0) (header?.averageRating ?: profile.rating ?: 0.0) else 0.0
        
        val daysWithOzMade = profile.daysWithOzMade ?: 0

        val level = computeLevel(
            orders = ordersCount,
            rating = averageRating,
            reviews = max(reviewsCount, ratingsCount),
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

    private data class Level(val title: String, val progress: Float, val hint: String)

    private fun computeLevel(orders: Int, rating: Double, reviews: Int, days: Int): Level {
        // Points system (Total 100)
        
        // 1. Orders: 2 points per order, max 40 (Need 20 orders for full points)
        val ordersPts = min(40.0, orders * 2.0).toInt()
        
        // 2. Reviews/Ratings: 5 points per review, max 30 (Need 6 reviews for full points)
        val reviewsPts = min(30.0, reviews * 5.0).toInt()
        
        // 3. Rating Score: Up to 20 points. Only counts if there is activity.
        // Formula: (Rating - 3.0) * 10 -> 3.0 = 0pts, 4.0 = 10pts, 5.0 = 20pts
        val ratingPts = if (reviews > 0 && rating >= 3.0) {
            min(20.0, (rating - 3.0) * 10.0).toInt()
        } else 0
        
        // 4. Tenure: 1 point per 2 weeks, max 10 points
        val daysPts = min(10.0, (days / 14.0)).toInt()

        val totalScore = ordersPts + reviewsPts + ratingPts + daysPts
        val s = totalScore.coerceIn(0, 100)
        
        // Progress is total progress towards "Top Master"
        val progress = s / 100f

        return when {
            s < 30 -> Level("Новый мастер", progress, "Начни собирать отзывы и выполненные заказы")
            s < 55 -> Level("Надёжный мастер", progress, "Держи рейтинг и увеличивай число заказов")
            s < 75 -> Level("Проверенный мастер", progress, "Ещё немного — и ты в топе")
            s < 90 -> Level("Отличный мастер", progress, "Стабильная работа, высокий рейтинг")
            else -> Level("Топ мастер", progress, "Максимальный уровень доверия покупателей")
        }
    }
}
