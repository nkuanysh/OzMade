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
    @SerializedName(value = "Title", alternate = ["Name", "name"]) val name: String,
    @SerializedName(value = "Description", alternate = ["description"]) val description: String,
    @SerializedName(value = "Cost", alternate = ["Price", "price", "cost"]) val price: Double,
    @SerializedName(value = "Type", alternate = ["type"]) val type: String? = null,
    // несколько категорий
    @SerializedName(value = "Categories", alternate = ["categories"]) val categories: List<String>? = null,
    // адрес (может не меняться при редактировании)
    @SerializedName(value = "Address", alternate = ["address"]) val address: String? = null,
    @SerializedName(value = "ImageName", alternate = ["ImageURL", "image_url"]) val imageUrl: String? = null,
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
    @SerializedName("Title") val name: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Cost") val price: Double,
    @SerializedName("Type") val type: String?,
    @SerializedName("Address")
    val address: String?,
    @SerializedName("ImageName")
    val imageUrl: String?,
    @SerializedName("Categories")
    val categories: List<String>?,
    @SerializedName("Images")
    val images: List<String>?,
    @SerializedName("Weight")
    val weight: String?,
    @SerializedName("HeightCm")
    val heightCm: String?,
    @SerializedName("WidthCm")
    val widthCm: String?,
    @SerializedName("DepthCm")
    val depthCm: String?,
    @SerializedName("Composition")
    val composition: String?,
    @SerializedName("YouTubeUrl")
    val youtubeUrl: String?
)

data class UpdateSellerProfileRequest(
    val profile_picture: String
)

data class CreateOrderRequest(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("delivery_type") val deliveryType: String,
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

data class UpdateSellerDeliveryRequest(
    @SerializedName("pickup_enabled") val pickupEnabled: Boolean? = null,
    @SerializedName("pickup_address") val pickupAddress: String? = null,
    @SerializedName("pickup_time") val pickupTime: String? = null,

    @SerializedName("free_delivery_enabled") val myDeliveryEnabled: Boolean? = null,
    @SerializedName("delivery_center_lat") val centerLat: Double? = null,
    @SerializedName("delivery_center_lng") val centerLng: Double? = null,
    @SerializedName("delivery_radius_km") val radiusKm: Int? = null,
    @SerializedName("delivery_center_address") val centerAddress: String? = null,

    @SerializedName("intercity_enabled") val intercityEnabled: Boolean? = null
)
