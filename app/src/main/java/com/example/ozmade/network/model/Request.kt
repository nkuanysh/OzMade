package com.example.ozmade.network.model

import com.google.gson.annotations.SerializedName

/**
 * Request for posting a review/comment.
 * Using PascalCase field names as observed in successful responses and typical for this backend.
 */
data class CommentRequest(
    @SerializedName("Text") val text: String,
    @SerializedName("Rating") val rating: Double,
    @SerializedName("OrderID") val orderId: Int? = null
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
    @SerializedName("Title") val title: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Cost") val price: Double,
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
    @SerializedName("IsHidden") val isHidden: Boolean? = null,
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
    @SerializedName("shipping_comment") val shippingComment: String? = null,
    @SerializedName("intercity_delivery") val intercityDelivery: IntercityDeliveryOrderRequest? = null
)

data class DeliveryAddressRequest(
    @SerializedName("city") val city: String,
    @SerializedName("fullAddress") val fullAddress: String,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

data class DeliveryPackageRequest(
    @SerializedName("weightGrams") val weightGrams: Int,
    @SerializedName("heightCm") val heightCm: Int,
    @SerializedName("widthCm") val widthCm: Int,
    @SerializedName("depthCm") val depthCm: Int
)

data class IntercityEstimateRequest(
    @SerializedName("fromAddress") val fromAddress: DeliveryAddressRequest,
    @SerializedName("toAddress") val toAddress: DeliveryAddressRequest,
    @SerializedName("package") val packageInfo: DeliveryPackageRequest,
    @SerializedName("tariffCodes") val tariffCodes: List<Int>? = null,
    @SerializedName("deliveryMode") val deliveryMode: String? = null
)

data class IntercityDeliveryOrderRequest(
    @SerializedName("provider") val provider: String,
    @SerializedName("price") val price: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("minDays") val minDays: Int,
    @SerializedName("maxDays") val maxDays: Int,
    @SerializedName("estimatedDateFrom") val estimatedDateFrom: String,
    @SerializedName("estimatedDateTo") val estimatedDateTo: String,
    @SerializedName("fromAddress") val fromAddress: DeliveryAddressRequest,
    @SerializedName("toAddress") val toAddress: DeliveryAddressRequest,
    @SerializedName("package") val packageInfo: DeliveryPackageRequest,
    @SerializedName("receiverName") val receiverName: String,
    @SerializedName("receiverPhone") val receiverPhone: String,
    @SerializedName("receiverAddress") val receiverAddress: String,
    @SerializedName("comment") val comment: String? = null
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

data class SyncRequest(
    @SerializedName("fcm_token") val fcmToken: String? = null
)

data class ProductCreateRequest(
    @SerializedName("Title") val title: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Cost") val price: Double,
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
    @SerializedName("IsHidden") val isHidden: Boolean? = null,
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
