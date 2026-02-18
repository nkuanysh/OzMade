package com.example.ozmade.main.home.details

import com.example.ozmade.network.api.ProductApi
import javax.inject.Inject

class RealProductRepository @Inject constructor(
    private val api: ProductApi
) : ProductRepository {

    suspend fun getProduct(id: String): ProductDetailsUi {
        val dto = api.getProductDetails(id)

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

    override suspend fun getProductDetails(productId: String): ProductDetailsUi {
        TODO("Not yet implemented")
    }

    override suspend fun isLiked(productId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun toggleLike(productId: String): Boolean {
        TODO("Not yet implemented")
    }
}
