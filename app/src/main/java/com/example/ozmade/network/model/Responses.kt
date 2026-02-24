package com.example.ozmade.network.model

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("ID") val id: Int,

    @SerializedName("Title") val title: String? = null,
    @SerializedName("Name") val name: String? = null,

    @SerializedName("Description") val description: String? = null,

    // старое поле (1 категория)
    @SerializedName("Type") val type: String? = null,

    // новое поле (много категорий)
    @SerializedName(value = "Categories", alternate = ["categories"])
    val categories: List<String>? = null,

    @SerializedName("Price") val price: Double? = null,
    @SerializedName("Cost") val cost: Double? = null,

    @SerializedName("Address") val address: String? = null,

    // старое (1 картинка)
    @SerializedName("ImageURL") val imageUrl: String? = null,

    // новое (1..10 картинок)
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
    val youtubeUrl: String? = null,

    // остальное как было
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

    // старое
    @SerializedName("Type") val type: String? = null,

    // новое
    @SerializedName(value = "Categories", alternate = ["categories"])
    val categories: List<String>? = null,

    @SerializedName(value = "Price", alternate = ["price"])
    val price: Double? = null,

    @SerializedName(value = "Address", alternate = ["address"])
    val address: String? = null,

    // старое 1 фото
    @SerializedName("ImageURL") val imageUrl: String? = null,

    // новое 1..10 фото
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
    val youtubeUrl: String? = null,

    // отзывы и рейтинг — остаются, они не “редактируемые”
    @SerializedName("AverageRating") val averageRating: Double? = null,
    @SerializedName("Comments") val comments: List<CommentDto>? = null
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
    @SerializedName("Address") val address: String?,
    @SerializedName("Role") val role: String,
    @SerializedName("IsSeller") val isSeller: Boolean,
    @SerializedName("CreatedAt") val createdAt: String
)

data class FavoriteStatusResponse(
    @SerializedName("status") val status: String
)

data class OrderDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("UserID") val userId: Int,
    @SerializedName("ProductID") val productId: Int,
    @SerializedName("Quantity") val quantity: Int,
    @SerializedName("TotalCost") val totalCost: Double,
    @SerializedName("Status") val status: String,
    @SerializedName("CreatedAt") val createdAt: String
)

data class MessageResponse(
    @SerializedName("message") val message: String
)

data class UploadUrlResponse(
    @SerializedName("upload_url") val uploadUrl: String
)

data class SellerProfileDto(
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("total_products") val totalProducts: Int
)

data class ChatDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("CreatedAt") val createdAt: String,
    @SerializedName("UpdatedAt") val updatedAt: String,
    @SerializedName("DeletedAt") val deletedAt: String?,
    @SerializedName("SellerID") val sellerId: Int,
    @SerializedName("BuyerID") val buyerId: Int,
    @SerializedName("Messages") val messages: List<MessageDto>?
)

data class MessageDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("CreatedAt") val createdAt: String,
    @SerializedName("UpdatedAt") val updatedAt: String,
    @SerializedName("DeletedAt") val deletedAt: String?,
    @SerializedName("ChatID") val chatId: Int,
    @SerializedName("SenderID") val senderId: Int,
    @SerializedName("Content") val content: String
)

// For RealSellerRepository
data class SellerPageDto(
    @SerializedName("seller") val seller: SellerHeaderDto,
    @SerializedName("products") val products: List<SellerProductDto>
)

data class SellerHeaderDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("orders_count") val ordersCount: Int,
    @SerializedName("rating") val rating: Double,
    @SerializedName("reviews_count") val reviewsCount: Int,
    @SerializedName("days_with_ozmade") val daysWithOzMade: Int
)

data class SellerProductDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Double,
    @SerializedName("city") val city: String,
    @SerializedName("address") val address: String,
    @SerializedName("rating") val rating: Double
)

data class ChatThreadDto(
    @SerializedName("thread_id") val threadId: String,
    @SerializedName("seller_id") val sellerId: String,
    @SerializedName("seller_name") val sellerName: String,
    @SerializedName("product_id") val productId: String,
    @SerializedName("product_title") val productTitle: String,
    @SerializedName("product_price") val productPrice: Int,
    @SerializedName("product_image_url") val productImageUrl: String?,
    @SerializedName("last_message") val lastMessage: String,
    @SerializedName("last_time_text") val lastTimeText: String
)

data class ChatMessageItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("is_mine") val isMine: Boolean,
    @SerializedName("time_text") val timeText: String
)

data class SellerRegistrationRequestDto(
    @SerializedName("id_card_url") val idCardUrl: String? = null
)

data class SellerRegistrationResponseDto(
    @SerializedName("seller_id") val sellerId: String,
    @SerializedName("status") val status: String
)

data class UpdateProductPriceRequest(
    @SerializedName("price") val price: Int
)

data class UpdateProductStatusRequest(
    @SerializedName("status") val status: String
)

data class EnsureThreadRequest(
    @SerializedName("seller_id") val sellerId: String,
    @SerializedName("product_id") val productId: String
)

data class EnsureThreadResponse(
    @SerializedName("thread_id") val threadId: String
)

data class SendMessageRequest(
    @SerializedName("text") val text: String
)

data class ProductDetailsFullDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Double,
    @SerializedName("images") val images: List<String>,
    @SerializedName("description") val description: String,
    @SerializedName("specs") val specs: List<SpecDto>,
    @SerializedName("rating") val rating: Double,
    @SerializedName("reviews_count") val reviewsCount: Int,
    @SerializedName("orders_count") val ordersCount: Int,
    @SerializedName("delivery") val delivery: DeliveryInfoDto,
    @SerializedName("seller") val seller: SellerInfoDto
)

data class SpecDto(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String
)

data class DeliveryInfoDto(
    @SerializedName("pickup_enabled") val pickupEnabled: Boolean,
    @SerializedName("pickup_time") val pickupTime: String,
    @SerializedName("free_delivery_enabled") val freeDeliveryEnabled: Boolean,
    @SerializedName("free_delivery_text") val freeDeliveryText: String,
    @SerializedName("intercity_enabled") val intercityEnabled: Boolean
)

data class SellerInfoDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("address") val address: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("completed_orders") val completedOrders: Int
)

data class ProductReviewsDto(
    @SerializedName("summary") val summary: ReviewsSummaryDto,
    @SerializedName("reviews") val reviews: List<ReviewItemDto>
)

data class ReviewsSummaryDto(
    @SerializedName("product_id") val productId: String,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("ratings_count") val ratingsCount: Int,
    @SerializedName("reviews_count") val reviewsCount: Int
)

data class ReviewItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("text") val text: String
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

data class SellerReviewsDto(
    @SerializedName("header") val header: SellerReviewsHeaderDto,
    @SerializedName("reviews") val reviews: List<SellerReviewItemDto>
)

data class SellerReviewsHeaderDto(
    @SerializedName("seller_id") val sellerId: String,
    @SerializedName("seller_name") val sellerName: String,
    @SerializedName("reviews_count") val reviewsCount: Int,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("ratings_count") val ratingsCount: Int
)

data class SellerReviewItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("product_id") val productId: String,
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