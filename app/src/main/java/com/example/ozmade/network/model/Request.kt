package com.example.ozmade.network.model

data class CommentRequest(
    val rating: Int,
    val text: String
)

data class ReportRequest(
    val reason: String
)

data class UpdateProfileRequest(
    val email: String? = null,
    val address: String? = null
)

data class ProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val type: String,
    val address: String,
    val image_url: String
)

data class UpdateSellerProfileRequest(
    val profile_picture: String
)