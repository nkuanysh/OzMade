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
        // ✅ FIRST: Sync user with backend (create/update user record)
        val syncResponse = api.syncUser()
        if (!syncResponse.isSuccessful) {
            throw Exception("Sync failed: ${syncResponse.code()}")
        }

        // ✅ THEN: Get profile from backend
        val response = api.getProfile()
        if (response.isSuccessful) {
            val profile = response.body()?.toDomain() ?: throw Exception("Empty body")
            _profileFlow.value = profile
            return profile
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
                name = name,
                address = address,
                avatarUrl = avatarUrl
            )
        )
        if (response.isSuccessful) {
            val profile = response.body()?.toDomain() ?: throw Exception("Empty body")
            _profileFlow.value = profile
            return profile
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
        _profileFlow.value = null
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
    // We use safe calls and Elvis operator to ensure no nulls are passed 
    // to non-nullable UserProfile fields, which prevents the constructor crash.
    val phoneStr = phoneNumber ?: ""
    val nameStr = name?.takeIf { it.isNotBlank() } 
        ?: email?.takeIf { it.isNotBlank() } 
        ?: phoneStr.ifBlank { "User" }

    return UserProfile(
        id = id.toString(),
        name = nameStr,
        phone = phoneStr,
        avatarUrl = avatarUrl,
        address = address ?: ""
    )
}
