package com.example.ozmade.main.userHome.details

import com.example.ozmade.main.user.favorites.FavoriteProductUi
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealProductRepository @Inject constructor(
    private val api: OzMadeApi
) : ProductRepository {

    override suspend fun getProductDetails(productId: Int): ProductDetailsUi {
        val id = productId

        runCatching { api.incrementProductView(id) }

        val resp = api.getProductDetails(id)
        if (!resp.isSuccessful) error("Не удалось загрузить товар (${resp.code()})")
        val dto = resp.body() ?: error("Пустой ответ от сервера")

        // Prioritize the images list, as the main imageUrl (ImageName) 
        // sometimes contains a bucket-level signed URL instead of an object URL.
        val rawImages: List<String> =
            dto.images?.takeIf { it.isNotEmpty() }
                ?: listOfNotNull(dto.imageUrl)
        
        val images = rawImages.map { ImageUtils.formatImageUrl(it) }.filter { it.isNotBlank() }

        val specs = buildList<Pair<String, String>> {
            dto.weight?.takeIf { it.isNotBlank() }?.let { add("Вес" to it) }
            dto.heightCm?.takeIf { it.isNotBlank() }?.let { add("Высота" to it) }
            dto.widthCm?.takeIf { it.isNotBlank() }?.let { add("Ширина" to it) }
            dto.depthCm?.takeIf { it.isNotBlank() }?.let { add("Глубина" to it) }
            dto.composition?.takeIf { it.isNotBlank() }?.let { add("Материал" to it) }
            dto.youtubeUrl?.takeIf { it.isNotBlank() }?.let { add("YouTube" to it) }
        }

        return ProductDetailsUi(
            id = dto.id,
            title = dto.title,
            price = dto.price ?: 0.0,
            rating = dto.averageRating ?: 0.0,
            reviewsCount = dto.comments?.size ?: 0,
            ordersCount = 0,
            images = images,
            description = dto.description,
            specs = specs,
            delivery = DeliveryInfoUi(
                pickupEnabled = false,
                pickupTime = null,
                freeDeliveryEnabled = false,
                freeDeliveryText = null,
                intercityEnabled = false
            ),
            seller = SellerUi(
                id = (dto.sellerId ?: 0),
                name = "Продавец",
                avatarUrl = null,
                address = dto.address ?: "",
                rating = 0.0,
                completedOrders = 0
            )
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

        if (!response.isSuccessful) {
            error("Не удалось изменить избранное (${response.code()})")
        }

        when (response.body()?.status?.lowercase()) {
            "added" -> true
            "removed" -> false
            else -> {
                api.getFavorites().body()?.any { it.id == productId } == true
            }
        }
    }

    override suspend fun getFavorites(): List<FavoriteProductUi> = withContext(Dispatchers.IO) {
        val response = api.getFavorites()
        if (!response.isSuccessful) error("Не удалось загрузить избранное (${response.code()})")

        val list = response.body().orEmpty()

        list.map { dto ->
            val img = ImageUtils.formatImageUrl(
                dto.images?.firstOrNull() ?: dto.imageUrl
            )
            FavoriteProductUi(
                id = dto.id,
                title = dto.title ?: dto.name ?: "Без названия",
                price = dto.cost ?: dto.price ?: 0.0,
                imageUrl = img,
                address = dto.address ?: "",
                rating = dto.averageRating ?: 0.0
            )
        }
    }

    override suspend fun postComment(productId: Int, rating: Int, text: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.postComment(productId, com.example.ozmade.network.model.CommentRequest(rating, text))
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportProduct(productId: Int, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.reportProduct(productId.toInt(), com.example.ozmade.network.model.ReportRequest(reason))
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
