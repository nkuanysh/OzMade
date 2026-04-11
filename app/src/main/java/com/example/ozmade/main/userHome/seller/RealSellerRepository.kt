package com.example.ozmade.main.userHome.seller

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.utils.ImageUtils
import javax.inject.Inject

class RealSellerRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerRepository {

    override suspend fun getSellerPage(sellerId: Int): SellerPageResponse {
        val resp = api.getSellerPage(sellerId)
        if (!resp.isSuccessful) error("Не удалось загрузить страницу продавца (${resp.code()})")
        
        val dto = resp.body() ?: error("Пустой ответ от сервера")

        val sDto = dto.seller
        val seller = SellerHeaderUi(
            id = sDto?.id ?: dto.id ?: sellerId,
            name = sDto?.name ?: dto.name ?: "Продавец",
            storeName = sDto?.storeName ?: dto.storeName,
            status = sDto?.status ?: dto.status ?: sDto?.levelTitle ?: dto.levelTitle ?: "",
            ordersCount = sDto?.ordersCount ?: dto.ordersCount ?: 0,
            rating = sDto?.rating ?: dto.rating ?: 0.0,
            reviewsCount = sDto?.reviewsCount ?: dto.reviewsCount ?: 0,
            daysWithOzMade = sDto?.daysWithOzMade ?: dto.daysWithOzMade ?: 0,
            avatarUrl = ImageUtils.formatImageUrl(sDto?.avatarUrl ?: dto.avatarUrl),
            city = sDto?.city ?: dto.city,
            description = sDto?.description ?: dto.description,
            categories = sDto?.categories ?: dto.categories,
            levelTitle = sDto?.levelTitle ?: dto.levelTitle,
            levelProgress = sDto?.levelProgress ?: dto.levelProgress,
            levelHint = sDto?.levelHint ?: dto.levelHint
        )

        val products = dto.products?.map {
            SellerProductUi(
                id = it.id,
                title = it.title ?: "Без названия",
                price = it.price ?: 0.0,
                city = it.city ?: "",
                address = it.address ?: "",
                rating = it.rating ?: 0.0,
                imageUrl = ImageUtils.formatImageUrl(it.images?.firstOrNull() ?: it.imageUrl)
            )
        } ?: emptyList()

        return SellerPageResponse(seller, products)
    }

    override suspend fun toggleLike(productId: Int): Boolean {
        val response = api.toggleFavorite(productId)

        if (!response.isSuccessful) {
            return false
        }

        return when (response.body()?.status?.lowercase()) {
            "added" -> true
            "removed" -> false
            else -> {
                api.getFavorites().body()?.any { it.id == productId } == true
            }
        }
    }

    override suspend fun isLiked(productId: Int): Boolean {
        return try {
            val response = api.getFavorites()
            response.body()?.any { it.id == productId } == true
        } catch (e: Exception) {
            false
        }
    }
}
