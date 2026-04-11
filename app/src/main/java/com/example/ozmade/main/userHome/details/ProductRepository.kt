package com.example.ozmade.main.userHome.details

import com.example.ozmade.main.user.favorites.FavoriteProductUi

interface ProductRepository {
    suspend fun getProductDetails(productId: Int): ProductDetailsUi

    suspend fun isLiked(productId: Int): Boolean
    suspend fun toggleLike(productId: Int): Boolean

    suspend fun getFavorites(): List<FavoriteProductUi>

    suspend fun postComment(productId: Int, rating: Int, text: String, orderId: Int? = null): Result<Unit>
    suspend fun reportProduct(productId: Int, reason: String): Result<Unit>
}