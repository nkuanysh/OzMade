package com.example.ozmade.main.userHome.details

import com.example.ozmade.network.api.OzMadeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealProductRepository @Inject constructor(
    private val api: OzMadeApi
) : ProductRepository {

    override suspend fun getProductDetails(productId: String): ProductDetailsUi {
        val id = productId.toIntOrNull() ?: error("Некорректный productId: $productId")

        // 1) увеличить просмотры (не критично если упадёт)
        runCatching { api.incrementProductView(id) }

        // 2) получить детали
        val resp = api.getProductDetails(id)
        if (!resp.isSuccessful) error("Не удалось загрузить товар (${resp.code()})")
        val dto = resp.body() ?: error("Пустой ответ от сервера")

        // 3) собрать images (у сервера сейчас часто приходит ImageName, а Images может быть null)
        val images: List<String> =
            dto.images?.takeIf { it.isNotEmpty() }
                ?: listOfNotNull(dto.imageUrl)

        // 4) собрать specs из полей (у тебя они есть в dto)
        val specs = buildList<Pair<String, String>> {
            dto.weight?.takeIf { it.isNotBlank() }?.let { add("Вес" to it) }
            dto.heightCm?.takeIf { it.isNotBlank() }?.let { add("Высота" to it) }
            dto.widthCm?.takeIf { it.isNotBlank() }?.let { add("Ширина" to it) }
            dto.depthCm?.takeIf { it.isNotBlank() }?.let { add("Глубина" to it) }
            dto.composition?.takeIf { it.isNotBlank() }?.let { add("Материал" to it) }
            dto.youtubeUrl?.takeIf { it.isNotBlank() }?.let { add("YouTube" to it) }
        }
        val sellerIdStr = (dto.sellerId ?: 0).toString()

        // 5) собрать UI (ставим безопасные дефолты)
        return ProductDetailsUi(
            id = dto.id.toString(),
            title = dto.title,
            price = dto.price ?: 0.0,                 // важно: без !!
            rating = dto.averageRating ?: 0.0,
            reviewsCount = dto.comments?.size ?: 0,
            ordersCount = 0,                          // сервер пока не отдаёт — ставим 0
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
                id = (dto.sellerId ?: 0).toString(),
                name = "Продавец",
                avatarUrl = null,
                address = dto.address ?: "",
                rating = 0.0,
                completedOrders = 0
            )
        )
    }

    override suspend fun isLiked(productId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.getFavorites()
            response.body()?.any { it.id.toString() == productId } == true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun toggleLike(productId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.toggleFavorite(productId.toInt())
            response.isSuccessful && response.body()?.status == "added"
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun postComment(productId: String, rating: Int, text: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.postComment(productId.toInt(), com.example.ozmade.network.model.CommentRequest(rating, text))
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportProduct(productId: String, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.reportProduct(productId.toInt(), com.example.ozmade.network.model.ReportRequest(reason))
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}