package com.example.ozmade.network.dto

data class SellerRegistrationRequestDto(
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val city: String,
    val address: String,
    val categories: List<String>,
    val about: String?,
)

data class SellerRegistrationResponseDto(
    val sellerId: String,
    val status: String // например: "approved" | "pending")
)