package com.example.ozmade.network.model

import com.google.gson.annotations.SerializedName

data class CommentRequest(
    @SerializedName(value = "Content", alternate = ["content"]) val content: String,
    @SerializedName("Rating") val rating: Int
)

data class ReportRequest(
    @SerializedName("Reason") val reason: String
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("address_lat") val addressLat: Double?,
    @SerializedName("address_lng") val addressLng: Double?,
    @SerializedName("PhotoUrl") val photoUrl: String? = null
)

data class ProductRequest(
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

data class UpdateSellerProfileRequest(
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("description") val about: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("categories") val categories: List<String>? = null,
    @SerializedName("level_title") val levelTitle: String? = null
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
    @SerializedName("FirstName") val firstName: String,
    @SerializedName("LastName") val lastName: String,
    @SerializedName("DisplayName") val displayName: String,
    @SerializedName("City") val city: String,
    @SerializedName("Address") val address: String,
    @SerializedName("Categories") val categories: List<String>,
    @SerializedName("About") val about: String? = null,
    @SerializedName("IdCardUrl") val idCardUrl: String? = null,
    @SerializedName("PhotoUrl") val photoUrl: String? = null
)

data class ChatSendMessageRequest(
    @SerializedName(value = "content", alternate = ["Content"]) val content: String
)

data class CreateChatRequest(
    @SerializedName(value = "seller_id", alternate = ["SellerID"]) val sellerId: Int? = null,
    @SerializedName(value = "product_id", alternate = ["ProductID"]) val productId: Int? = null,
    @SerializedName(value = "content", alternate = ["Content"]) val content: String? = null
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
