package com.example.ozmade.network.dto

data class UserProfileDto(
    val id: String,
    val name: String,
    val phone: String,
    val avatarUrl: String? = null,
    val address: String? = null
)

data class UpdateProfileRequest(
    val name: String,
    val address: String,
    val avatarUrl: String? = null
)
