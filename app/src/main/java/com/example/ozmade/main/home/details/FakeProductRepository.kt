package com.example.ozmade.main.home.details

import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeProductRepository @Inject constructor() : ProductRepository {

    private val likedSet = mutableSetOf<String>()

    private val data = listOf(
        ProductDetailsUi(
            id = "1",
            title = "Домашний сыр",
            price = 2500,
            rating = 4.8,
            reviewsCount = 12,
            ordersCount = 0,
            images = listOf("1", "2", "3"),
            description = "Очень вкусный домашний сыр. Натуральный состав.",
            specs = listOf("Вес" to "500 г", "Состав" to "Молоко, соль"),
            delivery = DeliveryInfoUi(
                pickupEnabled = true,
                pickupTime = "12:00-18:00",
                freeDeliveryEnabled = true,
                freeDeliveryText = "в этом районе",
                intercityEnabled = true
            )
        ),
        ProductDetailsUi(
            id = "2",
            title = "Тойбастар набор",
            price = 5500,
            rating = 4.6,
            reviewsCount = 8,
            ordersCount = 3,
            images = listOf("1", "2"),
            description = "Набор для тойбастара. Красиво упаковано.",
            specs = listOf("Комплект" to "10 шт", "Материал" to "Смешанный"),
            delivery = DeliveryInfoUi(
                pickupEnabled = true,
                pickupTime = "10:00-19:00",
                freeDeliveryEnabled = false,
                intercityEnabled = true
            )
        )
    )

    override suspend fun getProductDetails(productId: String): ProductDetailsUi {
        delay(450)
        return data.firstOrNull { it.id == productId }
            ?: ProductDetailsUi(
                id = productId,
                title = "Товар не найден",
                price = 0,
                rating = 0.0,
                reviewsCount = 0,
                ordersCount = 0,
                images = listOf("1"),
                description = "Описание недоступно.",
                specs = emptyList(),
                delivery = DeliveryInfoUi()
            )
    }

    override suspend fun isLiked(productId: String): Boolean {
        delay(80)
        return likedSet.contains(productId)
    }

    override suspend fun toggleLike(productId: String): Boolean {
        delay(120)
        if (likedSet.contains(productId)) likedSet.remove(productId) else likedSet.add(productId)
        return likedSet.contains(productId)
    }
}
