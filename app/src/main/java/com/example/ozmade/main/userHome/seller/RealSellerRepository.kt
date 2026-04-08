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

        val seller = SellerHeaderUi(
            id = dto.seller?.id ?: sellerId,
            name = dto.seller?.name ?: "Продавец",
            status = dto.seller?.status ?: "",
            ordersCount = dto.seller?.ordersCount ?: 0,
            rating = dto.seller?.rating ?: 0.0,
            reviewsCount = dto.seller?.reviewsCount ?: 0,
            daysWithOzMade = dto.seller?.daysWithOzMade ?: 0,
            avatarUrl = ImageUtils.formatImageUrl(dto.seller?.avatarUrl)
        )

        val products = dto.products?.map {
            SellerProductUi(
                id = it.id,
                title = it.title,
                price = it.price,
                city = it.city,
                address = it.address,
                rating = it.rating,
                imageUrl = ImageUtils.formatImageUrl(it.imageUrl)
            )
        } ?: emptyList()

        return SellerPageResponse(seller, products)
    }

    override suspend fun toggleLike(productId: Int): Boolean = false
    override suspend fun isLiked(productId: Int): Boolean = false
}
