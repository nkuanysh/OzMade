package com.example.ozmade.main.userHome.seller

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.utils.ImageUtils
import javax.inject.Inject

class RealSellerRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerRepository {

    override suspend fun getSellerPage(sellerId: Int): SellerPageResponse {
        val dto = api.getSellerPage(sellerId)

        val seller = SellerHeaderUi(
            id = dto.seller.id,
            name = dto.seller.name,
            status = dto.seller.status,
            ordersCount = dto.seller.ordersCount,
            rating = dto.seller.rating,
            reviewsCount = dto.seller.reviewsCount,
            daysWithOzMade = dto.seller.daysWithOzMade,
            avatarUrl = null // No avatar in SellerHeaderDto yet
        )

        val products = dto.products.map {
            SellerProductUi(
                id = it.id,
                title = it.title,
                price = it.price,
                city = it.city,
                address = it.address,
                rating = it.rating,
                imageUrl = null // Product imageUrl not present in SellerProductDto, using default
            )
        }

        return SellerPageResponse(seller, products)
    }

    // лайки позже можно вынести в отдельный FavoritesApi
    override suspend fun toggleLike(productId: Int): Boolean = false
    override suspend fun isLiked(productId: Int): Boolean = false
}
