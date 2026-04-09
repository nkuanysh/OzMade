package com.example.ozmade.main.seller.data

import android.net.Uri
import com.example.ozmade.main.seller.products.SellerProductUi
import com.example.ozmade.network.model.ProductCreateRequest
import com.example.ozmade.network.model.ProductDetailsDto
import com.example.ozmade.network.model.ProductDto
import com.example.ozmade.network.model.ProductRequest
import com.example.ozmade.network.model.SellerProfileDto
import com.example.ozmade.network.model.UpdateSellerProfileRequest
import com.example.ozmade.network.model.UploadUrlResponse
import java.io.File

interface SellerRepository {
    suspend fun sellerProfileExists(): Boolean
    suspend fun registerSeller(): Result<Unit>
    suspend fun getMyProducts(): List<SellerProductUi>

    suspend fun updateProductPrice(productId: Int, newPrice: Int)
    suspend fun toggleProductSaleState(productId: Int)
    suspend fun deleteProduct(productId: Int)

    suspend fun createProduct(request: ProductCreateRequest): Result<ProductDto>

    suspend fun createProductWithPhotos(
        photoUris: List<Uri>,
        draft: ProductCreateRequest
    ): Result<ProductDto>

    suspend fun updateProduct(productId: Int, request: ProductRequest): Result<Unit>
    
    suspend fun updateProductWithPhotos(
        productId: Int,
        photoUris: List<Uri>,
        request: ProductRequest
    ): Result<Unit>

    suspend fun getProductDetails(productId: Int): ProductDetailsDto
    suspend fun getSellerProfile(): SellerProfileDto?
    suspend fun updateSellerProfile(request: UpdateSellerProfileRequest): Result<Unit>

    suspend fun getUploadUrl(contentType: String): Result<UploadUrlResponse>
    suspend fun uploadImageToUrl(uploadUrl: String, file: File, mimeType: String): Result<Unit>
    
    suspend fun uploadPhoto(uri: Uri): Result<String>
}
