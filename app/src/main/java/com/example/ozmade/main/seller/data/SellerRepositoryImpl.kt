package com.example.ozmade.main.seller.data

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.ozmade.main.seller.products.SellerProductStatus
import com.example.ozmade.main.seller.products.SellerProductUi
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.ProductCreateRequest
import com.example.ozmade.network.model.ProductDetailsDto
import com.example.ozmade.network.model.ProductDto
import com.example.ozmade.network.model.ProductRequest
import com.example.ozmade.network.model.SellerProfileDto
import com.example.ozmade.network.model.UpdateSellerProfileRequest
import com.example.ozmade.network.model.UploadUrlResponse
import com.example.ozmade.network.upload.UploadService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(
    private val api: OzMadeApi,
    @ApplicationContext private val context: Context
) : SellerRepository {

    private val uploadService = UploadService()

    override suspend fun sellerProfileExists(): Boolean {
        return try {
            val response = api.getSellerProfile()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun registerSeller(): Result<Unit> {
        return try {
            val response = api.registerSeller()
            if (response.isSuccessful || response.code() == 400) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyProducts(): List<SellerProductUi> {
        val response = api.getSellerProducts()
        return response.body()?.map { dto ->
            SellerProductUi(
                id = dto.id,
                title = dto.title ?: dto.name ?: "No Title",
                price = (dto.price ?: 0.0).toInt(),
                imageUrl = dto.imageUrl ?: dto.images?.firstOrNull() ?: "",
                status = SellerProductStatus.ON_SALE
            )
        } ?: emptyList()
    }

    override suspend fun updateProductPrice(productId: Int, newPrice: Int) {
        api.patchProduct(productId, mapOf("Price" to newPrice))
    }

    override suspend fun toggleProductSaleState(productId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productId: Int) {
        val resp = api.deleteProduct(productId)
        if (!resp.isSuccessful) {
            throw Exception("Ошибка удаления: ${resp.code()} ${resp.message()}")
        }
    }

    override suspend fun createProduct(request: ProductCreateRequest): Result<ProductDto> = runCatching {
        val resp = api.createProduct(request)
        if (!resp.isSuccessful) error("Ошибка: ${resp.code()} ${resp.message()}")
        resp.body() ?: error("Пустой ответ")
    }

    override suspend fun createProductWithPhotos(
        photoUris: List<Uri>,
        draft: ProductCreateRequest
    ): Result<ProductDto> = runCatching {
        if (photoUris.isEmpty()) error("Не выбраны фото")

        val uploadedUrls = mutableListOf<String>()

        photoUris.forEach { uri ->
            val uriString = uri.toString()
            if (uriString.startsWith("http")) {
                uploadedUrls += extractFilename(uriString)
            } else {
                val url = uploadPhoto(uri).getOrThrow()
                uploadedUrls += url
            }
        }

        val finalRequest = draft.copy(
            imageUrl = uploadedUrls.firstOrNull(),
            images = uploadedUrls
        )

        val resp = api.createProduct(finalRequest)
        if (!resp.isSuccessful) {
            error("Ошибка создания товара: ${resp.code()} ${resp.message()}")
        }

        resp.body() ?: error("Пустой ответ")
    }

    override suspend fun updateProduct(productId: Int, request: ProductRequest): Result<Unit> {
        return try {
            val resp = api.updateProduct(productId, request)
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProductWithPhotos(
        productId: Int,
        photoUris: List<Uri>,
        request: ProductRequest
    ): Result<Unit> = runCatching {
        val uploadedUrls = mutableListOf<String>()

        photoUris.forEach { uri ->
            val uriString = uri.toString()
            if (uriString.startsWith("http")) {
                uploadedUrls += extractFilename(uriString)
            } else {
                val url = uploadPhoto(uri).getOrThrow()
                uploadedUrls += url
            }
        }

        val finalRequest = request.copy(
            imageUrl = uploadedUrls.firstOrNull(),
            images = uploadedUrls
        )

        val resp = api.updateProduct(productId, finalRequest)
        if (!resp.isSuccessful) {
            error("Ошибка обновления товара: ${resp.code()} ${resp.message()}")
        }
        Unit
    }

    override suspend fun getProductDetails(productId: Int): ProductDetailsDto {
        val resp = api.getProductDetails(productId)
        if (resp.isSuccessful) {
            return resp.body() ?: throw Exception("Empty body")
        } else {
            throw Exception("Error ${resp.code()}")
        }
    }

    override suspend fun getSellerProfile(): SellerProfileDto? {
        val resp = api.getSellerProfile()
        return if (resp.isSuccessful) resp.body() else null
    }

    override suspend fun updateSellerProfile(profilePictureUrl: String): Result<Unit> {
        return try {
            val resp = api.updateSellerProfile(
                UpdateSellerProfileRequest(profilePictureUrl)
            )
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUploadUrl(contentType: String): Result<UploadUrlResponse> {
        return try {
            val resp = api.getUploadProductPhotoUrl(contentType)
            if (resp.isSuccessful) {
                Result.success(resp.body() ?: return Result.failure(Exception("Empty body")))
            } else {
                Result.failure(Exception("Error ${resp.code()}: ${resp.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImageToUrl(
        uploadUrl: String,
        file: File,
        mimeType: String
    ): Result<Unit> {
        return uploadService.putFile(uploadUrl, file, mimeType)
    }

    override suspend fun uploadPhoto(uri: Uri): Result<String> = runCatching {
        val mimeType = getMimeType(uri)
        val tempFile = uriToTempFile(uri, mimeType)
        try {
            val uploadInfo = getUploadUrl(mimeType).getOrThrow()
            val uUrl = uploadInfo.uploadUrl ?: error("Server returned null uploadUrl")
            val fUrl = uploadInfo.fileUrl ?: error("Server returned null fileUrl")
            
            uploadImageToUrl(uUrl, tempFile, mimeType).getOrThrow()
            fUrl
        } finally {
            tempFile.delete()
        }
    }

    private fun getMimeType(uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "image/jpeg"
    }

    private fun uriToTempFile(uri: Uri, mimeType: String): File {
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?.let { ".$it" }
            ?: ".jpg"

        val tempFile = File.createTempFile("product_upload_", extension, context.cacheDir)

        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: error("Не удалось открыть изображение")

        return tempFile
    }

    private fun extractFilename(uriString: String): String {
        if (!uriString.startsWith("http")) return uriString
        
        // Если это URL Google Cloud Storage, извлекаем имя файла
        if (uriString.contains("storage.googleapis.com")) {
            val uri = Uri.parse(uriString)
            val path = uri.path
            if (!path.isNullOrBlank()) {
                val lastSegment = path.substringAfterLast("/")
                if (lastSegment.isNotBlank()) return lastSegment
            }
            // Если путь пустой или битый, возвращаем пустую строку, 
            // чтобы бэкенд не плодил вложенные ссылки
            return ""
        }
        
        return uriString
    }
}
