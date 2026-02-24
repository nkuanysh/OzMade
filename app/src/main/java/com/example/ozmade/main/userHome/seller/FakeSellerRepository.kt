package com.example.ozmade.main.userHome.seller

import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeSellerRepository @Inject constructor() : SellerRepository {

    private val liked = mutableSetOf<String>()

    override suspend fun getSellerPage(sellerId: String): SellerPageResponse {
        delay(450)

        // Заглушка продавца
        val seller = SellerHeaderUi(
            id = sellerId,
            name = if (sellerId == "seller_1") "Айгерим" else "Продавец",
            status = "Новый мастер",
            ordersCount = 127,
            rating = 4.9,
            reviewsCount = 58,
            daysWithOzMade = 43
        )

        // Заглушка товаров продавца
        val products = listOf(
            SellerProductUi("1", "Домашний сыр", 2500.0, "Алматы", "Алмалинский р-н", 4.8),
            SellerProductUi("2", "Тойбастар набор", 5500.0, "Алматы", "Ауэзовский р-н", 4.6),
            SellerProductUi("8", "Букет к 8 марта", 7000.0, "Алматы", "Жетысу", 4.9),
            SellerProductUi("7", "Пельмени домашние", 280.0, "Алматы", "Медеуский р-н", 4.4),
        )

        return SellerPageResponse(seller, products)
    }

    override suspend fun toggleLike(productId: String): Boolean {
        delay(120)
        if (liked.contains(productId)) liked.remove(productId) else liked.add(productId)
        return liked.contains(productId)
    }

    override suspend fun isLiked(productId: String): Boolean {
        delay(60)
        return liked.contains(productId)
    }
}
