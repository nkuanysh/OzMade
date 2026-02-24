package com.example.ozmade.main.user.profile.data

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.UpdateProfileRequest
import javax.inject.Inject

class RealProfileRepository @Inject constructor(
    private val api: OzMadeApi
) : ProfileRepository {

    override suspend fun getMyProfile(): UserProfile {
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

    override suspend fun logout() {
        // Обычно при Firebase logout — firebaseAuth.signOut()
        // Но ты сказал auth остаётся через Firebase — logout делайте в auth части.
        // Здесь можно удалить локальный кеш/токен, если появится.
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
