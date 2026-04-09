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
    @SerializedName("youtube_url") val youtubeUrl: String?,
    @SerializedName("address")
    val address: String? = null
)

data class UpdateSellerProfileRequest(
    @SerializedName("profile_picture") val profile_picture: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("about") val about: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("address") val address: String? = null
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
    @SerializedName("youtube_url") val youtubeUrl: String?,
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
