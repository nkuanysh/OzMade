package com.example.ozmade.main.userHome.details

interface ProductRepository {
    suspend fun getProductDetails(productId: String): ProductDetailsUi

    suspend fun isLiked(productId: String): Boolean
    suspend fun toggleLike(productId: String): Boolean
    suspend fun postComment(productId: String, rating: Int, text: String): Result<Unit>
    suspend fun reportProduct(productId: String, reason: String): Result<Unit>
}