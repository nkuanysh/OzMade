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

    // несколько категорий
    @SerializedName(value = "Categories", alternate = ["categories"])
    val categories: List<String>,

    // адрес (может не меняться при редактировании)
    @SerializedName(value = "Address", alternate = ["address"])
    val address: String? = null,

    // 1..10 фото
    @SerializedName(value = "Images", alternate = ["images"])
    val images: List<String>,

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