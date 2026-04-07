package com.example.ozmade.network.model

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("ID") val id: Int,
    @SerializedName(value = "SellerID", alternate = ["seller_id", "sellerId"])
    val sellerId: Int? = null,
    @SerializedName("Title") val title: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("Description") val description: String? = null,
    @SerializedName("Type") val type: String? = null,
    @SerializedName(value = "Categories", alternate = ["categories"])
    val categories: List<String>? = null,
    @SerializedName("Price") val price: Double? = null,
    @SerializedName("Cost") val cost: Double? = null,
    @SerializedName("Address") val address: String? = null,
    @SerializedName(value = "ImageURL", alternate = ["ImageName", "image_url"])
    val imageUrl: String? = null,
    @SerializedName(value = "Images", alternate = ["images"])
    val images: List<String>? = null,
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
    val youtubeUrl: String? = null,
    @SerializedName("WhatsAppLink") val whatsappLink: String? = null,
    @SerializedName("ViewCount") val viewCount: Int? = null,
    @SerializedName("AverageRating") val averageRating: Double? = null,
    @SerializedName("CreatedAt") val createdAt: String? = null,
    @SerializedName("Comments") val comments: List<CommentDto>? = null
)

data class ProductDetailsDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("Title") val title: String,
    @SerializedName("Description") val description: String,
    @SerializedName(value = "SellerID", alternate = ["seller_id", "sellerId"])
    val sellerId: Int? = null,
    @SerializedName("Type") val type: String? = null,
    @SerializedName(value = "Categories", alternate = ["categories"])
    val categories: List<String>? = null,
    @SerializedName(value = "Cost", alternate = ["Price", "price", "cost"])
    val price: Double? = null,
    @SerializedName(value = "Address", alternate = ["address"])
    val address: String? = null,
    @SerializedName(value = "WhatsAppLink", alternate = ["whatsapp_link", "whatsappLink"])
    val whatsappLink: String? = null,
    @SerializedName(value = "ViewCount", alternate = ["view_count", "viewCount"])
    val viewCount: Int? = null,
    @SerializedName(value = "CreatedAt", alternate = ["created_at", "createdAt"])
    val createdAt: String? = null,
    @SerializedName(value = "ImageURL", alternate = ["ImageName", "image_url"])
    val imageUrl: String? = null,
    @SerializedName(value = "Images", alternate = ["images"])
    val images: List<String>? = null,
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
    val youtubeUrl: String? = null,
    @SerializedName("AverageRating") val averageRating: Double? = null,
    @SerializedName("Comments") val comments: List<CommentDto>? = null,
    @SerializedName("SellerName") val sellerName: String? = null,
    @SerializedName("delivery") val delivery: DeliveryInfoDto? = null,
    @SerializedName("seller") val seller: SellerInfoDto? = null
)

data class CommentDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("UserID") val userId: Int,
    @SerializedName("ProductID") val productId: Int,
    @SerializedName("Rating") val rating: Int,
    @SerializedName("Text") val text: String,
    @SerializedName("CreatedAt") val createdAt: String?
)

data class AuthSyncResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("profile") val profile: ProfileDto
)

data class ProfileDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("FirebaseUID") val firebaseUid: String,
    @SerializedName("PhoneNumber") val phoneNumber: String,
    @SerializedName("Email") val email: String?,
    @SerializedName(value = "Name", alternate = ["name"]) val name: String? = null,
    @SerializedName(value = "AvatarURL", alternate = ["avatar_url", "avatarUrl"]) val avatarUrl: String? = null,
    @SerializedName("Address") val address: String?,
    @SerializedName("Role") val role: String,
    @SerializedName("IsSeller") val isSeller: Boolean,
    @SerializedName("CreatedAt") val createdAt: String
)

data class FavoriteStatusResponse(
    @SerializedName("status") val status: String
)

data class MessageResponse(
    @SerializedName("message") val message: String
)

data class UploadUrlResponse(
    @SerializedName(value = "uploadUrl", alternate = ["upload_url"])
    val uploadUrl: String?,
    @SerializedName(value = "fileUrl", alternate = ["file_url"])
    val fileUrl: String?
)

data class SellerProfileDto(
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("total_products") val totalProducts: Int
)

data class ChatDto(
    @SerializedName(value="ID", alternate=["id"]) val id: Int,
    @SerializedName(value="SellerID", alternate=["seller_id","sellerId"]) val sellerId: Int,
    @SerializedName(value="BuyerID", alternate=["buyer_id","buyerId"]) val buyerId: Int,
    @SerializedName(value="ProductID", alternate=["product_id","productId"]) val productId: Int? = null,
    @SerializedName(value="ProductName", alternate=["product_name","productName"]) val productName: String? = null,
    @SerializedName(value="ProductImage", alternate=["product_image", "productImage", "ImageName"]) val productImage: String? = null,
    @SerializedName(value="Messages", alternate=["messages"]) val messages: List<MessageDto>?
)

data class MessageDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("CreatedAt") val createdAt: String,
    @SerializedName("UpdatedAt") val updatedAt: String,
    @SerializedName("DeletedAt") val deletedAt: String?,
    @SerializedName("ChatID") val chatId: Int,
    @SerializedName("SenderID") val senderId: Int,
    @SerializedName("Content") val content: String,
    @SerializedName(value = "SenderRole", alternate = ["sender_role", "senderRole"]) val senderRole: String? = null
)

data class SellerPageDto(
    @SerializedName("seller") val seller: SellerHeaderDto,
    @SerializedName("products") val products: List<SellerProductDto>
)

data class SellerHeaderDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("orders_count") val ordersCount: Int,
    @SerializedName("rating") val rating: Double,
    @SerializedName("reviews_count") val reviewsCount: Int,
    @SerializedName("days_with_ozmade") val daysWithOzMade: Int,
    @SerializedName(value = "avatar_url", alternate = ["avatarUrl", "AvatarURL"]) val avatarUrl: String? = null
)

data class SellerProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Double,
    @SerializedName("city") val city: String,
    @SerializedName("address") val address: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName(value = "imageUrl", alternate = ["image_url", "ImageName"]) val imageUrl: String? = null
)

data class AdDto(
    @SerializedName("id") val id: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("title") val title: String,
    @SerializedName("deeplink") val deeplink: String
)

data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("icon_url") val iconUrl: String
)

data class
ProductReviewsDto(
    @SerializedName("summary") val summary: ReviewsSummaryDto,
    @SerializedName("reviews") val reviews: List<ReviewItemDto>
)

data class ReviewsSummaryDto(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("ratings_count") val ratingsCount: Int,
    @SerializedName("reviews_count") val reviewsCount: Int
)

data class ReviewItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("text") val text: String
)

data class EnsureThreadResponse(
    @SerializedName("thread_id") val threadId: Int
)

data class SpecDto(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String
)

data class DeliveryInfoDto(
    @SerializedName(value = "pickup_enabled", alternate = ["pickupEnabled"]) val pickupEnabled: Boolean,
    @SerializedName(value = "pickup_time", alternate = ["pickupTime"]) val pickupTime: String?,
    @SerializedName(value = "pickup_address", alternate = ["pickupAddress"]) val pickupAddress: String?,
    @SerializedName(value = "free_delivery_enabled", alternate = ["freeDeliveryEnabled"]) val freeDeliveryEnabled: Boolean,
    @SerializedName(value = "free_delivery_text", alternate = ["freeDeliveryText"]) val freeDeliveryText: String?,
    @SerializedName(value = "intercity_enabled", alternate = ["intercityEnabled"]) val intercityEnabled: Boolean,
    @SerializedName(value = "deliveryCenterLat") val centerLat: Double? = null,
    @SerializedName(value = "deliveryCenterLng") val centerLng: Double? = null,
    @SerializedName(value = "deliveryRadiusKm") val radiusKm: Double? = null,
    @SerializedName(value = "deliveryCenterAddress", alternate = ["centerAddress"]) val centerAddress: String? = null
)

data class SellerInfoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName(value = "avatar_url", alternate = ["avatarUrl"]) val avatarUrl: String? = null,
    @SerializedName("address") val address: String?,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName(value = "completed_orders", alternate = ["ordersCount"]) val completedOrders: Int? = null
)

data class SellerReviewsDto(
    @SerializedName("header") val header: SellerReviewsHeaderDto,
    @SerializedName("reviews") val reviews: List<SellerReviewItemDto>
)

data class SellerReviewsHeaderDto(
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("seller_name") val sellerName: String,
    @SerializedName("reviews_count") val reviewsCount: Int,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("ratings_count") val ratingsCount: Int
)

data class SellerReviewItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_title") val productTitle: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("text") val text: String
)

data class SellerQualityDto(
    @SerializedName("seller_name") val sellerName: String,
    @SerializedName("orders_count") val ordersCount: Int,
    @SerializedName("days_with_ozmade") val daysWithOzMade: Int,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("ratings_count") val ratingsCount: Int,
    @SerializedName("reviews_count") val reviewsCount: Int,
    @SerializedName("reviews") val reviews: List<SellerReviewItemDto>
)

data class SellerDeliveryDto(
    @SerializedName("pickup_enabled") val pickupEnabled: Boolean,
    @SerializedName("pickup_address") val pickupAddress: String?,
    @SerializedName("pickup_time") val pickupTime: String?,
    @SerializedName("free_delivery_enabled") val myDeliveryEnabled: Boolean,
    @SerializedName("delivery_center_lat") val centerLat: Double?,
    @SerializedName("delivery_center_lng") val centerLng: Double?,
    @SerializedName("delivery_radius_km") val radiusKm: Int?,
    @SerializedName("delivery_center_address") val centerAddress: String?,
    @SerializedName("intercity_enabled") val intercityEnabled: Boolean
)

data class OrderDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("Status") val status: String,
    @SerializedName("CreatedAt") val createdAt: String,
    @SerializedName("ProductID") val productId: Int,
    @SerializedName("ProductTitle") val productTitle: String? = null,
    @SerializedName("ProductImageUrl") val productImageUrl: String? = null,
    @SerializedName("Price") val price: Double? = null,
    @SerializedName("Quantity") val quantity: Int,
    @SerializedName("TotalCost") val totalCost: Double,
    @SerializedName("SellerID") val sellerId: Int? = null,
    @SerializedName("SellerName") val sellerName: String? = null,
    @SerializedName("DeliveryType") val deliveryType: String,
    @SerializedName("PickupAddress") val pickupAddress: String? = null,
    @SerializedName("PickupTime") val pickupTime: String? = null,
    @SerializedName("ZoneCenterLat") val zoneCenterLat: Double? = null,
    @SerializedName("ZoneCenterLng") val zoneCenterLng: Double? = null,
    @SerializedName("ZoneRadiusKm") val zoneRadiusKm: Int? = null,
    @SerializedName("ZoneCenterAddress") val zoneCenterAddress: String? = null,
    @SerializedName("ShippingAddressText") val shippingAddressText: String? = null,
    @SerializedName("ShippingComment") val shippingComment: String? = null,
    @SerializedName("ConfirmCode") val confirmCode: String? = null
)
