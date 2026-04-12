package com.example.ozmade.main.seller.profile.data

import com.example.ozmade.main.orders.data.OrderStatus
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class RealSellerProfileRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerProfileRepository {

    override suspend fun getSellerProfile(): SellerProfileUi = withContext(Dispatchers.IO) {
        val profileDeferred = async { api.getSellerProfile() }
        val qualityDeferred = async { api.getSellerQuality() }
        val ordersDeferred = async { api.getSellerOrders() }

        val profileResp = profileDeferred.await()
        val qualityResp = qualityDeferred.await()
        val ordersResp = ordersDeferred.await()

        if (!profileResp.isSuccessful) error("Не удалось загрузить профиль (${profileResp.code()})")
        
        val pDto = profileResp.body() ?: error("Пустой ответ профиля")
        val qDto = qualityResp.body() 
        val ordersDto = ordersResp.body() ?: emptyList()

        // Calculate orders count consistently: all except cancelled/expired
        val consistentOrdersCount = ordersDto.count {
            it.status != OrderStatus.CANCELLED_BY_BUYER &&
            it.status != OrderStatus.CANCELLED_BY_SELLER &&
            it.status != OrderStatus.EXPIRED
        }

        // Consistent rating logic
        val ratingsCount = qDto?.ratingsCount ?: 0
        val averageRating = if (ratingsCount > 0) qDto?.averageRating ?: 0.0 else 0.0

        // Consistent level logic (must match RealSellerQualityRepository)
        val level = computeLevel(
            orders = consistentOrdersCount,
            rating = averageRating,
            reviews = max(qDto?.reviews_count ?: 0, ratingsCount),
            days = qDto?.daysWithOzMade ?: pDto.daysWithOzMade ?: 0
        )

        val activeOrders = ordersDto.filter {
            it.status == OrderStatus.PENDING_SELLER ||
                    it.status == OrderStatus.CONFIRMED ||
                    it.status == OrderStatus.READY_OR_SHIPPED
        }.map { dto ->
            com.example.ozmade.main.orders.data.OrderUi(
                id = dto.id,
                status = dto.status,
                createdAt = dto.createdAt,
                productId = dto.productId,
                productTitle = dto.productTitle ?: "Товар",
                productImageUrl = ImageUtils.formatImageUrl(dto.productImageUrl),
                price = dto.price ?: 0.0,
                quantity = dto.quantity,
                totalCost = dto.totalCost,
                sellerId = dto.sellerId,
                sellerName = dto.sellerName,
                deliveryType = dto.deliveryType,
                pickupAddress = dto.pickupAddress,
                pickupTime = dto.pickupTime,
                zoneCenterAddress = dto.zoneCenterAddress,
                zoneCenterLat = dto.zoneCenterLat,
                zoneCenterLng = dto.zoneCenterLng,
                zoneRadiusKm = dto.zoneRadiusKm,
                shippingAddressText = dto.shippingAddressText,
                shippingLat = dto.shippingLat,
                shippingLng = dto.shippingLng,
                shippingComment = dto.shippingComment,
                confirmCode = dto.confirmCode,
                isReviewed = dto.isReviewed
            )
        }

        SellerProfileUi(
            name = pDto.name,
            firstName = pDto.firstName,
            lastName = pDto.lastName,
            about = pDto.about,
            city = pDto.city,
            address = pDto.address,
            categories = pDto.categories,
            photoUrl = ImageUtils.formatProfilePhotoUrl(pDto.photoUrl),
            levelTitle = level.title,
            levelProgress = level.progress,
            levelHint = level.hint,
            totalProducts = pDto.totalProducts,
            rating = averageRating,
            ratingsCount = ratingsCount,
            ordersCount = consistentOrdersCount,
            activeOrders = activeOrders
        )
    }

    private data class Level(val title: String, val progress: Float, val hint: String)

    private fun computeLevel(orders: Int, rating: Double, reviews: Int, days: Int): Level {
        val ordersPts = min(40.0, orders * 2.0).toInt()
        val reviewsPts = min(30.0, reviews * 5.0).toInt()
        val ratingPts = if (reviews > 0 && rating >= 3.0) {
            min(20.0, (rating - 3.0) * 10.0).toInt()
        } else 0
        val daysPts = min(10.0, (days / 14.0)).toInt()

        val totalScore = ordersPts + reviewsPts + ratingPts + daysPts
        val s = totalScore.coerceIn(0, 100)
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
