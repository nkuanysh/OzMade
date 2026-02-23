package com.example.ozmade.main.home.seller

import com.example.ozmade.network.api.OzMadeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealSellerRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerRepository {

    override suspend fun getSellerPage(sellerId: String): SellerPageResponse {
        val dto = api.getSellerPage(sellerId)

        val seller = SellerHeaderUi(
            id = dto.seller.id,
            name = dto.seller.name,
            status = dto.seller.status,
            ordersCount = dto.seller.ordersCount,
            rating = dto.seller.rating,
            reviewsCount = dto.seller.reviewsCount,
            daysWithOzMade = dto.seller.daysWithOzMade
        )

        val products = dto.products.map {
            SellerProductUi(
                id = it.id,
                title = it.title,
                price = it.price,
                city = it.city,
                address = it.address,
                rating = it.rating
            )
        }

        return SellerPageResponse(seller, products)
    }

    override suspend fun toggleLike(productId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.toggleFavorite(productId.toInt())
            response.isSuccessful && response.body()?.status == "added"
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isLiked(productId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.getFavorites()
            response.body()?.any { it.id.toString() == productId } == true
        } catch (e: Exception) {
            false
        }
    }
}