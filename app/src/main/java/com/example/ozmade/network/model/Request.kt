package com.example.ozmade.network.model

import com.google.gson.annotations.SerializedName

data class CommentRequest(
    @SerializedName("Rating")
    val rating: Int,
    @SerializedName("Content")
    val content: String
)

data class ReportRequest(
    @SerializedName("Reason")
    val reason: String
)

data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)

data class ProductRequest(
    @SerializedName("Name")
    val name: String,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Price")
    val price: Double,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Categories")
    val categories: List<String>,
    @SerializedName("ImageUrl")
    val imageUrl: String?,
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
    val youtubeUrl: String?,
    @SerializedName("address")
    val address: String? = null
)

data class UpdateSellerProfileRequest(
    @SerializedName(value = "AvatarURL", alternate = ["profile_picture", "avatar_url", "logo_url", "logoUrl"]) val profile_picture: String? = null,
    @SerializedName(value = "DisplayName", alternate = ["display_name", "displayName"]) val displayName: String? = null,
    @SerializedName(value = "About", alternate = ["about"]) val about: String? = null,
    @SerializedName(value = "City", alternate = ["city"]) val city: String? = null,
    @SerializedName(value = "Address", alternate = ["address"]) val address: String? = null,
    @SerializedName(value = "FirstName", alternate = ["first_name", "firstName"]) val firstName: String? = null,
    @SerializedName(value = "LastName", alternate = ["last_name", "lastName"]) val lastName: String? = null,
    @SerializedName(value = "Categories", alternate = ["categories"]) val categories: List<String>? = null
)

data class CreateOrderRequest(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("delivery_type") val deliveryType: String,
    @SerializedName("shipping_address_text") val shippingAddressText: String? = null,
    @SerializedName("shipping_lat") val shippingLat: Double? = null,
    @SerializedName("shipping_lng") val shippingLng: Double? = null,
    @SerializedName("shipping_comment") val shippingComment: String? = null
)

data class CompleteOrderRequest(
    @SerializedName("code") val code: String
)

data class ReadyOrShippedRequest(
    @SerializedName("comment") val comment: String? = null
)

data class SellerRegistrationRequestDto(
    @SerializedName(value = "FirstName", alternate = ["first_name", "firstName"]) val firstName: String,
    @SerializedName(value = "LastName", alternate = ["last_name", "lastName"]) val lastName: String,
    @SerializedName(value = "DisplayName", alternate = ["display_name", "displayName"]) val displayName: String,
    @SerializedName(value = "City", alternate = ["city"]) val city: String,
    @SerializedName(value = "Address", alternate = ["address"]) val address: String,
    @SerializedName(value = "Categories", alternate = ["categories"]) val categories: List<String>,
    @SerializedName(value = "About", alternate = ["about"]) val about: String? = null,
    @SerializedName(value = "IdCardUrl", alternate = ["id_card_url", "idCardUrl"]) val idCardUrl: String? = null,
    @SerializedName(value = "AvatarURL", alternate = ["logo_url", "avatar_url", "logoUrl", "profile_picture"]) val logoUrl: String? = null
)
data class ChatSendMessageRequest(
    @SerializedName("content") val content: String
)

data class CreateChatRequest(
    @SerializedName("seller_id") val sellerId: Int? = null,
    @SerializedName("product_id") val productId: Int? = null,
    @SerializedName("content") val content: String? = null
)

data class FCMTokenRequest(
    @SerializedName("token") val token: String
)

data class ProductCreateRequest(
    @SerializedName("Name") val name: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Price") val price: Double,
    @SerializedName("Type") val type: String,
    @SerializedName("Categories") val categories: List<String>,
    @SerializedName("ImageUrl") val imageUrl: String?,
    @SerializedName("Images") val images: List<String>?,
    @SerializedName("Weight") val weight: String?,
    @SerializedName("HeightCm") val heightCm: String?,
    @SerializedName("WidthCm") val widthCm: String?,
    @SerializedName("DepthCm") val depthCm: String?,
    @SerializedName("Composition") val composition: String?,
    @SerializedName("YouTubeUrl") val youtubeUrl: String?,
    @SerializedName("address") val address: String? = null
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
    @SerializedName("intercity_enabled") val intercityEnabled: Boolean? = null,
    @SerializedName("courier_enabled") val courierEnabled: Boolean? = null,
    @SerializedName("post_enabled") val postEnabled: Boolean? = null
)
