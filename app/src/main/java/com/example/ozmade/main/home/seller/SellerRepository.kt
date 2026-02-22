package com.example.ozmade.main.home.seller

interface SellerRepository {
    suspend fun getSellerPage(sellerId: String): SellerPageResponse
    suspend fun toggleLike(productId: String): Boolean
    suspend fun isLiked(productId: String): Boolean
}

data class SellerPageResponse(
    val seller: SellerHeaderUi,
    val products: List<SellerProductUi>
)
