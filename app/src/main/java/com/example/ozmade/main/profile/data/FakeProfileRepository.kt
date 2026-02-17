package com.example.ozmade.main.profile.data

import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeProfileRepository @Inject constructor() : ProfileRepository {

    // "хранилище", чтобы после Save профиль реально менялся
    private var cached = UserProfile(
        id = "me",
        name = "Нурсултан",
        phone = "+7 777 123 45 67",
        avatarUrl = null,
        address = "Алматы"
    )

    override suspend fun getMyProfile(): UserProfile {
        delay(400)
        return cached
    }

    override suspend fun updateMyProfile(
        name: String,
        address: String,
        avatarUrl: String?
    ): UserProfile {
        delay(500)
        cached = cached.copy(
            name = name,
            address = address,
            avatarUrl = avatarUrl
        )
        return cached
    }

    override suspend fun logout() {
        // тут позже будет очистка токена/сессии
    }
}
