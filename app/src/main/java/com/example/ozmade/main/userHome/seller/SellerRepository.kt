package com.example.ozmade.main.userHome.seller

interface SellerRepository {
    suspend fun getSellerPage(sellerId: Int): SellerPageResponse
    suspend fun toggleLike(productId: Int): Boolean
    suspend fun isLiked(productId: Int): Boolean
}

data class SellerPageResponse(
    val seller: SellerHeaderUi,
    val products: List<SellerProductUi>
)
