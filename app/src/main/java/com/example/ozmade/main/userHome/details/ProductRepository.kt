package com.example.ozmade.main.userHome.details

interface ProductRepository {
    suspend fun getProductDetails(productId: Int): ProductDetailsUi

    suspend fun isLiked(productId: Int): Boolean
    suspend fun toggleLike(productId: Int): Boolean
    suspend fun postComment(productId: Int, rating: Int, text: String): Result<Unit>
    suspend fun reportProduct(productId: Int, reason: String): Result<Unit>
}