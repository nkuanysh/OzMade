package com.example.ozmade.main.seller.data

import com.example.ozmade.main.seller.products.SellerProductUi
import com.example.ozmade.network.model.ProductDetailsDto
import com.example.ozmade.network.model.ProductRequest
import com.example.ozmade.network.model.SellerRegistrationRequestDto
import com.example.ozmade.network.model.SellerRegistrationResponseDto

interface SellerRepository {
    suspend fun sellerProfileExists(): Boolean
    suspend fun registerSeller(): Result<Unit>
    suspend fun getMyProducts(): List<SellerProductUi>

    suspend fun updateProductPrice(productId: Int, newPrice: Int)

    suspend fun toggleProductSaleState(productId: Int)

    suspend fun deleteProduct(productId: Int)

    suspend fun createProduct(request: ProductRequest): Result<Unit>
    suspend fun updateProduct(productId: Int, request: ProductRequest): Result<Unit>
    suspend fun getProductDetails(productId: Int): ProductDetailsDto
    suspend fun getSellerProfile(): com.example.ozmade.network.model.SellerProfileDto?
    suspend fun updateSellerProfile(profilePictureUrl: String): Result<Unit>
}