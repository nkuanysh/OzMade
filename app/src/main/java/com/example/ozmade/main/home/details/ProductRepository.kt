package com.example.ozmade.main.home.details

interface ProductRepository {
    suspend fun getProductDetails(productId: String): ProductDetailsUi

    // лайк под бэкенд (пока можно не использовать, но готово)
    suspend fun isLiked(productId: String): Boolean
    suspend fun toggleLike(productId: String): Boolean // вернёт новое liked состояние
}