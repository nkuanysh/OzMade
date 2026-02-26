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
    @SerializedName(value = "Name", alternate = ["name"]) val name: String,
    @SerializedName(value = "Description", alternate = ["description"]) val description: String,
    @SerializedName(value = "Price", alternate = ["price"]) val price: Double,
    @SerializedName(value = "Type", alternate = ["type"]) val type: String? = null,
    // несколько категорий
    @SerializedName(value = "Categories", alternate = ["categories"]) val categories: List<String>? = null,
    // адрес (может не меняться при редактировании)
    @SerializedName(value = "Address", alternate = ["address"]) val address: String? = null,
    @SerializedName(value = "ImageURL", alternate = ["image_url", "ImageName"]) val imageUrl: String? = null,
    @SerializedName(value = "Images", alternate = ["images"]) val images: List<String>? = null,
    // характеристики
    @SerializedName(value = "Weight", alternate = ["weight"]) val weight: String? = null,
    @SerializedName(value = "HeightCm", alternate = ["height_cm", "heightCm"]) val heightCm: String? = null,
    @SerializedName(value = "WidthCm", alternate = ["width_cm", "widthCm"]) val widthCm: String? = null,
    @SerializedName(value = "DepthCm", alternate = ["depth_cm", "depthCm"]) val depthCm: String? = null,
    @SerializedName(value = "Composition", alternate = ["composition"]) val composition: String? = null,
    @SerializedName(value = "YouTubeUrl", alternate = ["youtube_url", "youtubeUrl", "YouTubeURL"]) val youtubeUrl: String? = null
)
data class ProductCreateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,

    @SerializedName("type") val type: String,
    @SerializedName("address") val address: String,

    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("categories") val categories: List<String>?,
    @SerializedName("images") val images: List<String>?,

    @SerializedName("weight") val weight: String?,
    @SerializedName("height_cm") val heightCm: String?,
    @SerializedName("width_cm") val widthCm: String?,
    @SerializedName("depth_cm") val depthCm: String?,
    @SerializedName("composition") val composition: String?,
    @SerializedName("youtube_url") val youtubeUrl: String?
)

data class UpdateSellerProfileRequest(
    val profile_picture: String
)

data class CreateOrderRequest(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("delivery_type") val deliveryType: String, // PICKUP/MY_DELIVERY/INTERCITY
    @SerializedName("shipping_address_text") val shippingAddressText: String? = null
)

data class CompleteOrderRequest(
    @SerializedName("code") val code: String
)

data class ReadyOrShippedRequest(
    @SerializedName("comment") val comment: String? = null
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
data class ChatSendMessageRequest(
    @SerializedName("content") val content: String
)


data class CreateChatRequest(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("content") val content: String
)