package com.example.ozmade.main.userHome.details

import android.util.Log
import com.example.ozmade.main.user.favorites.FavoriteProductUi
import com.example.ozmade.main.user.profile.data.ProfileRepository
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealProductRepository @Inject constructor(
    private val api: OzMadeApi,
    private val profileRepo: ProfileRepository
) : ProductRepository {

    private val TAG = "RealProductRepository"

    override suspend fun getProductDetails(productId: Int): ProductDetailsUi {
        val id = productId

        runCatching { api.incrementProductView(id) }

        val resp = api.getProductDetails(id)
        if (!resp.isSuccessful) error("Не удалось загрузить товар (${resp.code()})")
        val dto = resp.body() ?: error("Пустой ответ от сервера")

        val rawImages: List<String> =
            dto.images?.takeIf { it.isNotEmpty() }
                ?: listOfNotNull(dto.imageUrl)

        val images = rawImages.map {
            ImageUtils.formatImageUrl(it)
        }.filter { it.isNotBlank() }

        val specs = buildList<Pair<String, String>> {
            dto.weight?.takeIf { it.isNotBlank() }?.let { add("Вес" to it) }
            dto.heightCm?.takeIf { it.isNotBlank() }?.let { add("Высота" to it) }
            dto.widthCm?.takeIf { it.isNotBlank() }?.let { add("Ширина" to it) }
            dto.depthCm?.takeIf { it.isNotBlank() }?.let { add("Глубина" to it) }
            dto.composition?.takeIf { it.isNotBlank() }?.let { add("Материал" to it) }
            dto.youtubeUrl?.takeIf { it.isNotBlank() }?.let { add("YouTube" to it) }
        }

        val profile = profileRepo.getMyProfile()
        val buyerLat = profile.addressLat
        val buyerLng = profile.addressLng

        val sellerIdFromDto = dto.sellerId ?: dto.seller?.id ?: 0
        val syncResp = runCatching { api.syncUser() }.getOrNull()
        val myUserId = syncResp?.body()?.userId ?: profile.id
        val isMine = sellerIdFromDto == myUserId || sellerIdFromDto == profile.id

        // Улучшенный подсчет отзывов: берем максимум из всех возможных полей
        val actualReviewsCount = maxOf(
            dto.reviewsCount ?: 0,
            dto.ratingsCount ?: 0,
            dto.comments?.size ?: 0
        )

        // Улучшенный подсчет заказов
        val actualOrdersCount = maxOf(
            dto.ordersCount ?: 0,
            dto.seller?.completedOrders ?: 0
        )

        return ProductDetailsUi(
            id = dto.id,
            title = dto.title,
            price = dto.price ?: 0.0,
            rating = dto.averageRating ?: 0.0,
            reviewsCount = actualReviewsCount,
            ordersCount = actualOrdersCount,
            images = images,
            youtubeUrl = dto.youtubeUrl,
            description = dto.description,
            specs = specs,
            delivery = DeliveryInfoUi(
                pickupEnabled = dto.delivery?.pickupEnabled ?: false,
                pickupTime = dto.delivery?.pickupTime,
                freeDeliveryEnabled = dto.delivery?.freeDeliveryEnabled ?: false,
                freeDeliveryText = dto.delivery?.freeDeliveryText,
                intercityEnabled = dto.delivery?.intercityEnabled ?: false,
                pickupAddress = dto.delivery?.pickupAddress,
                centerAddress = dto.delivery?.centerAddress,
                centerLat = dto.delivery?.centerLat,
                centerLng = dto.delivery?.centerLng,
                radiusKm = dto.delivery?.radiusKm,
                buyerSavedAddress = profile.address,
                buyerSavedAddressLat = buyerLat,
                buyerSavedAddressLng = buyerLng,
                isBuyerInsideDeliveryZone = isInsideDeliveryZone(
                    buyerLat = buyerLat,
                    buyerLng = buyerLng,
                    sellerLat = dto.delivery?.centerLat,
                    sellerLng = dto.delivery?.centerLng,
                    radiusKm = dto.delivery?.radiusKm
                )
            ),
            seller = SellerUi(
                id = sellerIdFromDto,
                name = dto.sellerName?.takeIf { it.isNotBlank() } ?: dto.seller?.name?.takeIf { it.isNotBlank() } ?: "Мастер",
                address = dto.seller?.address ?: dto.address ?: "",
                rating = dto.seller?.rating ?: 0.0,
                completedOrders = dto.seller?.completedOrders ?: 0,
                photoUrl = ImageUtils.formatProfilePhotoUrl(dto.seller?.photoUrl)
            ),
            isMine = isMine
        )
    }

    override suspend fun isLiked(productId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.getFavorites()
            response.body()?.any { it.id == productId } == true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun toggleLike(productId: Int): Boolean = withContext(Dispatchers.IO) {
        val response = api.toggleFavorite(productId)
        if (!response.isSuccessful) error("Не удалось изменить избранное")
        
        when (response.body()?.status?.lowercase()) {
            "added" -> true
            "removed" -> false
            else -> api.getFavorites().body()?.any { it.id == productId } == true
        }
    }

    override suspend fun getFavorites(): List<FavoriteProductUi> = withContext(Dispatchers.IO) {
        val response = api.getFavorites()
        if (!response.isSuccessful) error("Не удалось загрузить избранное")
        response.body().orEmpty().map { dto ->
            FavoriteProductUi(
                id = dto.id,
                title = dto.title ?: "Без названия",
                price = dto.price ?: 0.0,
                imageUrl = ImageUtils.formatImageUrl(dto.images?.firstOrNull() ?: dto.imageUrl),
                address = dto.address ?: "",
                rating = dto.averageRating ?: 0.0,
                ordersCount = maxOf(dto.ordersCount ?: 0, 0),
                reviewsCount = maxOf(dto.reviewsCount ?: 0, dto.ratingsCount ?: 0)
            )
        }
    }

    override suspend fun postComment(productId: Int, rating: Int, text: String, orderId: Int?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.postComment(productId, com.example.ozmade.network.model.CommentRequest(text, rating, orderId))
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportProduct(productId: Int, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.reportProduct(productId, com.example.ozmade.network.model.ReportRequest(reason))
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isInsideDeliveryZone(lat1: Double?, lng1: Double?, lat2: Double?, lng2: Double?, r: Double?): Boolean? {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null || r == null) return null
        val res = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lng1, lat2, lng2, res)
        return (res[0] / 1000f) <= r
    }
}
