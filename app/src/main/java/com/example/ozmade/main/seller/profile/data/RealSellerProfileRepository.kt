package com.example.ozmade.main.seller.profile.data

import com.example.ozmade.main.orders.data.OrderStatus
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

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
            levelTitle = pDto.levelTitle,
            levelProgress = pDto.levelProgress,
            levelHint = pDto.levelHint,
            totalProducts = pDto.totalProducts,
            rating = qDto?.averageRating ?: pDto.rating ?: 0.0,
            ratingsCount = qDto?.ratingsCount ?: 0,
            ordersCount = qDto?.ordersCount ?: pDto.ordersCount ?: 0,
            activeOrders = activeOrders
        )
    }
}
