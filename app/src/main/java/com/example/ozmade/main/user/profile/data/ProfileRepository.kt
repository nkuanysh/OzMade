package com.example.ozmade.main.user.profile.data

import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val profileFlow: StateFlow<UserProfile?>
    suspend fun getMyProfile(): UserProfile
    suspend fun updateMyProfile(name: String, address: String, avatarUrl: String?): UserProfile
    suspend fun logout()
    suspend fun getMyOrders(): List<com.example.ozmade.network.model.OrderDto>
    suspend fun getMyFavorites(): List<com.example.ozmade.network.model.ProductDto>
}
