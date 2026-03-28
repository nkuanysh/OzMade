package com.example.ozmade.main.user.profile.data

import android.content.Context
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.FCMTokenRequest
import com.example.ozmade.network.model.UpdateProfileRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RealProfileRepository @Inject constructor(
    private val api: OzMadeApi,
    @ApplicationContext private val context: Context
) : ProfileRepository {

    override suspend fun getMyProfile(): UserProfile {
        // ✅ FIRST: Sync user with backend (create/update user record)
        val syncResponse = api.syncUser()
        if (!syncResponse.isSuccessful) {
            throw Exception("Sync failed: ${syncResponse.code()}")
        }

        // ✅ THEN: Get profile from backend
        val response = api.getProfile()
        if (response.isSuccessful) {
            return response.body()?.toDomain() ?: throw Exception("Empty body")
        } else {
            throw Exception("Error ${response.code()}")
        }
    }

    override suspend fun updateMyProfile(
        name: String,
        address: String,
        avatarUrl: String?
    ): UserProfile {
        val response = api.updateProfile(
            UpdateProfileRequest(
                address = address
            )
        )
        if (response.isSuccessful) {
            return response.body()?.toDomain() ?: throw Exception("Empty body")
        } else {
            throw Exception("Error ${response.code()}")
        }
    }

    override suspend fun getMyOrders(): List<com.example.ozmade.network.model.OrderDto> {
        val resp = api.getOrders()
        return if (resp.isSuccessful) resp.body().orEmpty() else emptyList()
    }

    override suspend fun getMyFavorites(): List<com.example.ozmade.network.model.ProductDto> {
        val resp = api.getFavorites()
        return if (resp.isSuccessful) resp.body().orEmpty() else emptyList()
    }

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun logout() {
        // 1. Clear FCM token on backend
        runCatching {
            api.updateFCMToken(FCMTokenRequest(""))
        }

        // 2. Clear physical cache directory
        try {
            context.cacheDir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Clear Coil image cache
        try {
            context.imageLoader.diskCache?.clear()
            context.imageLoader.memoryCache?.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// маппер DTO -> Domain
private fun com.example.ozmade.network.model.ProfileDto.toDomain(): UserProfile {
    return UserProfile(
        id = id.toString(),
        name = email ?: phoneNumber,
        phone = phoneNumber,
        avatarUrl = null,
        address = address ?: ""
    )
}