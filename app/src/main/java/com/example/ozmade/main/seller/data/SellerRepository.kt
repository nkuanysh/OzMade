package com.example.ozmade.main.seller.data

import com.example.ozmade.main.seller.products.SellerProductUi
import com.example.ozmade.network.model.ProductCreateRequest
import com.example.ozmade.network.model.ProductDetailsDto
import com.example.ozmade.network.model.ProductDto
import com.example.ozmade.network.model.ProductRequest

interface SellerRepository {
    suspend fun sellerProfileExists(): Boolean
    suspend fun registerSeller(): Result<Unit>
    suspend fun getMyProducts(): List<SellerProductUi>

    suspend fun updateProductPrice(productId: Int, newPrice: Int)

    suspend fun toggleProductSaleState(productId: Int)

    suspend fun deleteProduct(productId: Int)

    suspend fun createProduct(request: ProductCreateRequest): Result<ProductDto>
    suspend fun updateProduct(productId: Int, request: ProductRequest): Result<Unit>
    suspend fun getProductDetails(productId: Int): ProductDetailsDto
    suspend fun getSellerProfile(): com.example.ozmade.network.model.SellerProfileDto?
    suspend fun updateSellerProfile(profilePictureUrl: String): Result<Unit>

    suspend fun getUploadUrl(): Result<com.example.ozmade.network.model.UploadUrlResponse>
    suspend fun uploadImageToUrl(uploadUrl: String, file: java.io.File, mimeType: String): Result<Unit>
}