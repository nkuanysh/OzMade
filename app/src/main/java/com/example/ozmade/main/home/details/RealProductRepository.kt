package com.example.ozmade.main.home.details

import com.example.ozmade.network.api.OzMadeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealProductRepository @Inject constructor(
    private val api: OzMadeApi
) : ProductRepository {

    override suspend fun getProductDetails(productId: String): ProductDetailsUi {
        productId.toIntOrNull()?.let { id ->
            try {
                api.incrementProductView(id)
            } catch (e: Exception) {
                // Ignore background task failure or log it
            }
        }
        val dto = api.getProductDetailsFull(productId)

        return ProductDetailsUi(
            id = dto.id,
            title = dto.title,
            price = dto.price,
            images = dto.images,
            description = dto.description,
            specs = dto.specs.map { it.key to it.value },
            rating = dto.rating,
            reviewsCount = dto.reviewsCount,
            ordersCount = dto.ordersCount,
            delivery = DeliveryInfoUi(
                pickupEnabled = dto.delivery.pickupEnabled,
                pickupTime = dto.delivery.pickupTime,
                freeDeliveryEnabled = dto.delivery.freeDeliveryEnabled,
                freeDeliveryText = dto.delivery.freeDeliveryText,
                intercityEnabled = dto.delivery.intercityEnabled
            ),
            seller = SellerUi(
                id = dto.seller.id,
                name = dto.seller.name,
                avatarUrl = dto.seller.avatarUrl,
                address = dto.seller.address,
                rating = dto.seller.rating,
                completedOrders = dto.seller.completedOrders
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