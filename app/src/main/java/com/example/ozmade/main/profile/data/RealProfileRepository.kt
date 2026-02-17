package com.example.ozmade.main.profile.data

import com.example.ozmade.network.api.ProfileApi
import com.example.ozmade.network.dto.UpdateProfileRequest
import javax.inject.Inject

class RealProfileRepository @Inject constructor(
    private val api: ProfileApi
) : ProfileRepository {

    override suspend fun getMyProfile(): UserProfile {
        val dto = api.getMyProfile()
        return dto.toDomain()
    }

    override suspend fun updateMyProfile(
        name: String,
        address: String,
        avatarUrl: String?
    ): UserProfile {
        val dto = api.updateMyProfile(
            UpdateProfileRequest(
                name = name,
                address = address,
                avatarUrl = avatarUrl
            )
        )
        return dto.toDomain()
    }

    override suspend fun logout() {
        // Обычно при Firebase logout — firebaseAuth.signOut()
        // Но ты сказал auth остаётся через Firebase — logout делайте в auth части.
        // Здесь можно удалить локальный кеш/токен, если появится.
    }
}

// маппер DTO -> Domain
private fun com.example.ozmade.network.dto.UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        name = name,
        phone = phone,
        avatarUrl = avatarUrl,
        address = address ?: ""
    )
}
