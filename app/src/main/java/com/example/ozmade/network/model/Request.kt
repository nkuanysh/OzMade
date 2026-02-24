package com.example.ozmade.network.model

import com.google.gson.annotations.SerializedName

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
    @SerializedName(value = "Name", alternate = ["name"])
    val name: String,

    @SerializedName(value = "Description", alternate = ["description"])
    val description: String,

    @SerializedName(value = "Price", alternate = ["price"])
    val price: Double,
    @SerializedName(value = "Type", alternate = ["type"])
    val type: String? = null,
    // несколько категорий
    @SerializedName(value = "Categories", alternate = ["categories"])
    val categories: List<String>? = null,

    // адрес (может не меняться при редактировании)
    @SerializedName(value = "Address", alternate = ["address"])
    val address: String? = null,

    @SerializedName(value = "ImageURL", alternate = ["image_url", "ImageName"])
    val imageUrl: String? = null,

    @SerializedName(value = "Images", alternate = ["images"])
    val images: List<String>? = null,

    // характеристики
    @SerializedName(value = "Weight", alternate = ["weight"])
    val weight: String? = null,

    @SerializedName(value = "HeightCm", alternate = ["height_cm", "heightCm"])
    val heightCm: String? = null,

    @SerializedName(value = "WidthCm", alternate = ["width_cm", "widthCm"])
    val widthCm: String? = null,

    @SerializedName(value = "DepthCm", alternate = ["depth_cm", "depthCm"])
    val depthCm: String? = null,

    @SerializedName(value = "Composition", alternate = ["composition"])
    val composition: String? = null,

    @SerializedName(value = "YouTubeUrl", alternate = ["youtube_url", "youtubeUrl", "YouTubeURL"])
    val youtubeUrl: String? = null
)
data class UpdateSellerProfileRequest(
    val profile_picture: String
)

data class SellerRegistrationRequestDto(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("city") val city: String,
    @SerializedName("address") val address: String,
    @SerializedName("categories") val categories: List<String>,
    @SerializedName("about") val about: String? = null,
    @SerializedName("id_card_url") val idCardUrl: String? = null
)