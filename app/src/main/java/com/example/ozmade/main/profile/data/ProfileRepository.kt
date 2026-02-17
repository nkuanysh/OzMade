package com.example.ozmade.main.profile.data

interface ProfileRepository {
    suspend fun getMyProfile(): UserProfile
    suspend fun updateMyProfile(name: String, address: String, avatarUrl: String?): UserProfile
    suspend fun logout()
}
