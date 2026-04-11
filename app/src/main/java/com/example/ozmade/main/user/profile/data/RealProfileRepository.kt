package com.example.ozmade.main.user.profile.data

import android.content.Context
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.FCMTokenRequest
import com.example.ozmade.network.model.UpdateProfileRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealProfileRepository @Inject constructor(
    private val api: OzMadeApi,
    @ApplicationContext private val context: Context
) : ProfileRepository {

    private val _profileFlow = MutableStateFlow<UserProfile?>(null)
    override val profileFlow: StateFlow<UserProfile?> = _profileFlow.asStateFlow()

    override suspend fun getMyProfile(): UserProfile {
        val syncResponse = api.syncUser()
        if (!syncResponse.isSuccessful) {
            throw Exception("Sync failed: ${syncResponse.code()}")
        }

        val response = api.getProfile()
        if (!response.isSuccessful) {
            throw Exception("Error ${response.code()}")
        }

        val profile = response.body()?.toDomain() ?: throw Exception("Empty body")
        _profileFlow.value = profile
        return profile
    }

    override suspend fun updateMyProfile(
        name: String,
        address: String,
        addressLat: Double?,
        addressLng: Double?,
        photoUrl: String?
    ): UserProfile {
        val response = api.updateProfile(
            UpdateProfileRequest(
                name = name,
                address = address,
                addressLat = addressLat,
                addressLng = addressLng,
                photoUrl = photoUrl
            )
        )

        if (!response.isSuccessful) {
            throw Exception("Error ${response.code()}")
        }

        val profile = response.body()?.toDomain() ?: throw Exception("Empty body")
        _profileFlow.value = profile
        return profile
    }

    override suspend fun getMyOrders(): List<com.example.ozmade.network.model.OrderDto> {
        val resp = api.getOrders()
        return if (resp.isSuccessful) resp.body().orEmpty() else emptyList()
    }

    override suspend fun getMyFavorites(): List<com.example.ozmade.network.model.ProductDto> {
        val resp = api.getFavorites()
        return if (resp.isSuccessful) resp.body().orEmpty() else emptyList()
    }

    override suspend fun uploadPhoto(uri: android.net.Uri): String = runCatching {
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val extension = android.webkit.MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?.let { ".$it" }
            ?: ".jpg"

        val tempFile = java.io.File.createTempFile("photo_upload_", extension, context.cacheDir)

        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: error("Не удалось открыть изображение")

            val resp = api.getUploadProductPhotoUrl(mimeType)
            if (!resp.isSuccessful) throw Exception("Error getting upload URL: ${resp.code()}")
            
            val uploadInfo = resp.body() ?: throw Exception("Empty body")
            val uUrl = uploadInfo.uploadUrl ?: error("Server returned null uploadUrl")
            val fUrl = uploadInfo.fileUrl ?: error("Server returned null fileUrl")

            val uploadService = com.example.ozmade.network.upload.UploadService()
            uploadService.putFile(uUrl, tempFile, mimeType).getOrThrow()

            fUrl
        } finally {
            tempFile.delete()
        }
    }.getOrThrow()

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun logout() {
        _profileFlow.value = null

        runCatching {
            api.updateFCMToken(FCMTokenRequest(""))
        }

        try {
            context.cacheDir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            context.imageLoader.diskCache?.clear()
            context.imageLoader.memoryCache?.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun com.example.ozmade.network.model.ProfileDto.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        name = name?.takeIf { it.isNotBlank() } ?: "User",
        address = address ?: "",
        phone = phoneNumber ?: "",
        addressLat = addressLat,
        addressLng = addressLng,
        photoUrl = com.example.ozmade.utils.ImageUtils.formatProfilePhotoUrl(photoUrl)
    )
}