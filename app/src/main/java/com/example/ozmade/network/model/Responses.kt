package com.example.ozmade.network.model

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName(value = "ID", alternate = ["id"]) val id: Int = 0,
    @SerializedName(value = "SellerID", alternate = ["seller_id", "sellerId"])
    val sellerId: Int? = null,
    @SerializedName(value = "Title", alternate = ["title"]) val title: String? = null,
    @SerializedName(value = "Name", alternate = ["name"]) val name: String? = null,
    @SerializedName(value = "Description", alternate = ["description"]) val description: String? = null,
    @SerializedName(value = "Type", alternate = ["type"]) val type: String? = null,
    @SerializedName(value = "Categories", alternate = ["categories"])
    val categories: List<String>? = null,
    @SerializedName(value = "Price", alternate = ["price", "Cost", "cost"]) val price: Double? = null,
    @SerializedName(value = "Address", alternate = ["address"]) val address: String? = null,
    @SerializedName(value = "ImageURL", alternate = ["ImageName", "image_url", "imageUrl"])
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
    @SerializedName(value = "ViewCount", alternate = ["view_count", "viewCount"]) val viewCount: Int? = null,
    @SerializedName(value = "AverageRating", alternate = ["average_rating", "averageRating", "rating", "Rating"]) val averageRating: Double? = null,
    @SerializedName(value = "CreatedAt", alternate = ["created_at", "createdAt"]) val createdAt: String? = null,
    @SerializedName("Comments") val comments: List<CommentDto>? = null,
    @SerializedName(value = "IsActive", alternate = ["is_active", "isActive"]) val isActive: Boolean? = null
)

data class ProductDetailsDto(
    @SerializedName(value = "ID", alternate = ["id"]) val id: Int,
    @SerializedName(value = "Title", alternate = ["title"]) val title: String,
    @SerializedName(value = "Description", alternate = ["description"]) val description: String,
    @SerializedName(value = "SellerID", alternate = ["seller_id", "sellerId"])
    val sellerId: Int? = null,
    @SerializedName(value = "Type", alternate = ["type"]) val type: String? = null,
    @SerializedName(value = "Categories", alternate = ["categories"])
    val categories: List<String>? = null,
    @SerializedName(value = "Cost", alternate = ["Price", "price", "cost"])
    val price: Double? = null,
    @SerializedName(value = "Address", alternate = ["address"]) val address: String? = null,
    @SerializedName(value = "WhatsAppLink", alternate = ["whatsapp_link", "whatsappLink"])
    val whatsappLink: String? = null,
    @SerializedName(value = "ViewCount", alternate = ["view_count", "viewCount"])
    val viewCount: Int? = null,
    @SerializedName(value = "CreatedAt", alternate = ["created_at", "createdAt"])
    val createdAt: String? = null,
    @SerializedName(value = "ImageURL", alternate = ["ImageName", "image_url", "imageUrl"])
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
    @SerializedName(value = "AverageRating", alternate = ["average_rating", "averageRating", "rating", "Rating"]) val averageRating: Double? = null,
    @SerializedName("Comments") val comments: List<CommentDto>? = null,
    @SerializedName("SellerName") val sellerName: String? = null,
    @SerializedName("delivery") val delivery: DeliveryInfoDto? = null,
    @SerializedName("seller") val seller: SellerInfoDto? = null,
    @SerializedName(value = "IsActive", alternate = ["is_active", "isActive"]) val isActive: Boolean? = null
)

data class CommentDto(
    @SerializedName(value = "ID", alternate = ["id"]) val id: Int,
    @SerializedName(value = "UserID", alternate = ["user_id", "userId"]) val userId: Int,
    @SerializedName(value = "ProductID", alternate = ["product_id", "productId"]) val productId: Int,
    @SerializedName(value = "Rating", alternate = ["rating"]) val rating: Int,
    @SerializedName(value = "Text", alternate = ["text"]) val text: String,
    @SerializedName(value = "CreatedAt", alternate = ["created_at", "createdAt"]) val createdAt: String?
)

data class AuthSyncResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("profile") val profile: ProfileDto
)

data class ProfileDto(
    @SerializedName("ID") val id: Int,
    @SerializedName(value = "FirebaseUID", alternate = ["firebase_uid", "firebaseUid"])
    val firebaseUid: String? = null,
    @SerializedName(value = "PhoneNumber", alternate = ["phone_number", "phoneNumber"])
    val phoneNumber: String? = null,
    @SerializedName(value = "Email", alternate = ["email"])
    val email: String? = null,
    @SerializedName(value = "Name", alternate = ["name"])
    val name: String? = null,
    @SerializedName(value = "photo_url", alternate = ["AvatarURL", "avatar_url", "avatarUrl"])
    val photoUrl: String? = null,
    @SerializedName(value = "Address", alternate = ["address"])
    val address: String? = null,
    @SerializedName(value = "Role", alternate = ["role"])
    val role: String? = null,
    @SerializedName(value = "IsSeller", alternate = ["is_seller", "isSeller"])
    val isSeller: Boolean? = null,
    @SerializedName(value = "CreatedAt", alternate = ["created_at", "createdAt"])
    val createdAt: String? = null,
    @SerializedName("address_lat") val addressLat: Double? = null,
    @SerializedName("address_lng") val addressLng: Double? = null
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
    @SerializedName(value = "ID", alternate = ["id"]) val id: Int = 0,
    @SerializedName(value = "Name", alternate = ["name"]) val name: String,
    @SerializedName(value = "Status", alternate = ["status"]) val status: String,
    @SerializedName(value = "TotalProducts", alternate = ["total_products", "totalProducts"]) val totalProducts: Int,
    @SerializedName(value = "Rating", alternate = ["rating", "average_rating"]) val rating: Double? = null,
    @SerializedName(value = "OrdersCount", alternate = ["orders_count", "ordersCount", "total_orders"]) val ordersCount: Int? = null,
    @SerializedName(value = "photo_url", alternate = ["AvatarURL", "avatar_url", "avatarUrl", "logo_url", "ImageName"]) val photoUrl: String? = null,
    @SerializedName("about") val about: String? = null,
    @SerializedName(value = "city", alternate = ["City"]) val city: String? = null,
    @SerializedName(value = "address", alternate = ["Address"]) val address: String? = null,
    @SerializedName(value = "first_name", alternate = ["firstName"]) val firstName: String? = null,
    @SerializedName(value = "last_name", alternate = ["lastName"]) val lastName: String? = null,
    @SerializedName("categories") val categories: List<String>? = null,
    @SerializedName(value = "days_with_ozmade", alternate = ["daysWithOzMade"]) val daysWithOzMade: Int? = null,
    @SerializedName(value = "level_title", alternate = ["levelTitle", "LevelTitle"]) val levelTitle: String? = null,
    @SerializedName(value = "level_progress", alternate = ["levelProgress", "LevelProgress"]) val levelProgress: Float? = null,
    @SerializedName(value = "level_hint", alternate = ["levelHint", "LevelHint"]) val levelHint: String? = null,
)

data class ChatDto(
    @SerializedName(value="ID", alternate=["id"]) val id: Int,
    @SerializedName(value="CreatedAt", alternate=["created_at"]) val createdAt: String,
    @SerializedName(value="UpdatedAt", alternate=["updated_at"]) val updatedAt: String,
    @SerializedName(value="SellerID", alternate=["seller_id","sellerId"]) val sellerId: Int,
    @SerializedName(value="seller", alternate=["Seller"]) val seller: SellerInfoDto? = null,
    @SerializedName(value="BuyerID", alternate=["buyer_id","buyerId"]) val buyerId: Int,
    @SerializedName(value="ProductID", alternate=["product_id","productId"]) val productId: Int? = null,
    @SerializedName(value="ProductName", alternate=["product_name","productName"]) val productName: String? = null,
    @SerializedName(value="ProductImage", alternate=["product_image", "productImage", "ImageName"]) val productImage: String? = null,
    @SerializedName(value ="phone_number", alternate = ["phoneNumber"]) val phoneNumber: String? = null,
    @SerializedName(value = "seller_name", alternate=["SellerName", "sellerName", "store_name", "storeName", "StoreName", "shop_name", "shopName", "ShopName", "display_name", "DisplayName"]) val sellerName: String? = null,
    @SerializedName("seller_photo") val sellerPhoto: String? = null,
    @SerializedName("buyer_name") val buyerName: String? = null,
    @SerializedName("buyer_photo") val buyerPhoto: String? = null,
    @SerializedName("deleted_by_buyer") val deletedByBuyer: Boolean = false,
    @SerializedName("deleted_by_seller") val deletedBySeller: Boolean = false,
    @SerializedName("buyer_cleared_at") val buyerClearedAt: String? = null,
    @SerializedName("seller_cleared_at") val sellerClearedAt: String? = null,
    @SerializedName(value="Messages", alternate=["messages"]) val messages: List<MessageDto>? = null
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
    @SerializedName(value = "Seller", alternate = ["seller"]) val seller: SellerHeaderDto? = null,
    @SerializedName(value = "Products", alternate = ["products"]) val products: List<SellerProductDto>? = null,

    // Direct fields for flattened response (SellerQualityResponse style)
    @SerializedName(value = "id", alternate = ["ID"]) val id: Int? = null,
    @SerializedName(value = "name", alternate = ["seller_name", "Name", "SellerName", "display_name", "DisplayName"]) val name: String? = null,
    @SerializedName(value = "store_name", alternate = ["storeName", "StoreName"]) val storeName: String? = null,
    @SerializedName(value = "status", alternate = ["Status"]) val status: String? = null,
    @SerializedName(value = "orders_count", alternate = ["ordersCount", "OrdersCount", "total_orders", "completed_orders", "completedOrders"]) val ordersCount: Int? = null,
    @SerializedName(value = "rating", alternate = ["average_rating", "averageRating", "AverageRating", "Rating"]) val rating: Double? = null,
    @SerializedName(value = "reviews_count", alternate = ["reviewsCount", "ReviewsCount"]) val reviewsCount: Int? = null,
    @SerializedName(value = "ratings_count", alternate = ["ratingsCount", "RatingsCount"]) val ratingsCount: Int? = null,
    @SerializedName(value = "days_with_ozmade", alternate = ["daysWithOzMade", "DaysWithOzMade"]) val daysWithOzMade: Int? = null,
    @SerializedName(value = "photo_url", alternate = ["avatar_url", "avatarUrl", "AvatarURL", "logo_url", "ImageName", "PhotoURL"]) val photoUrl: String? = null,
    @SerializedName(value = "city", alternate = ["City"]) val city: String? = null,
    @SerializedName(value = "address", alternate = ["Address"]) val address: String? = null,
    @SerializedName(value = "categories", alternate = ["Categories"]) val categories: String? = null,
    @SerializedName(value = "description", alternate = ["Description", "about", "About"]) val description: String? = null,
    @SerializedName(value = "level_title", alternate = ["levelTitle", "LevelTitle"]) val levelTitle: String? = null,
    @SerializedName(value = "level_progress", alternate = ["levelProgress", "LevelProgress"]) val levelProgress: Float? = null,
    @SerializedName(value = "level_hint", alternate = ["levelHint", "LevelHint"]) val levelHint: String? = null,
    @SerializedName(value = "Header", alternate = ["header"]) val header: SellerReviewsHeaderDto?,
    @SerializedName(value = "Reviews", alternate = ["reviews"]) val reviews: List<SellerReviewItemDto>?,


)

data class SellerHeaderDto(
    @SerializedName(value = "id", alternate = ["ID"]) val id: Int,
    @SerializedName(value = "name", alternate = ["seller_name", "Name", "SellerName", "display_name", "DisplayName"]) val name: String? = null,
    @SerializedName(value = "store_name", alternate = ["storeName", "StoreName"]) val storeName: String? = null,
    @SerializedName(value = "status", alternate = ["Status"]) val status: String? = null,
    @SerializedName(value = "orders_count", alternate = ["ordersCount", "OrdersCount", "total_orders", "completed_orders", "completedOrders"]) val ordersCount: Int? = null,
    @SerializedName(value = "rating", alternate = ["average_rating", "averageRating", "AverageRating", "Rating"]) val rating: Double? = null,
    @SerializedName(value = "reviews_count", alternate = ["reviewsCount", "ReviewsCount"]) val reviewsCount: Int? = null,
    @SerializedName(value = "ratings_count", alternate = ["ratingsCount", "RatingsCount"]) val ratingsCount: Int? = null,
    @SerializedName(value = "days_with_ozmade", alternate = ["daysWithOzMade", "DaysWithOzMade"]) val daysWithOzMade: Int? = null,
    @SerializedName(value = "photo_url", alternate = ["avatar_url", "avatarUrl", "AvatarURL", "logo_url", "ImageName", "PhotoURL"]) val photoUrl: String? = null,
    @SerializedName(value = "city", alternate = ["City"]) val city: String? = null,
    @SerializedName(value = "address", alternate = ["Address"]) val address: String? = null,
    @SerializedName(value = "categories", alternate = ["Categories"]) val categories: String? = null,
    @SerializedName(value = "description", alternate = ["Description", "about", "About"]) val description: String? = null,
    @SerializedName(value = "level_title", alternate = ["levelTitle", "LevelTitle"]) val levelTitle: String? = null,
    @SerializedName(value = "level_progress", alternate = ["levelProgress", "LevelProgress"]) val levelProgress: Float? = null,
    @SerializedName(value = "level_hint", alternate = ["levelHint", "LevelHint"]) val levelHint: String? = null
)

data class SellerProductDto(
    @SerializedName(value = "id", alternate = ["ID"]) val id: Int,
    @SerializedName(value = "title", alternate = ["Title", "name", "Name"]) val title: String? = null,
    @SerializedName(value = "price", alternate = ["Price", "cost", "Cost"]) val price: Double? = null,
    @SerializedName(value = "city", alternate = ["City"]) val city: String? = null,
    @SerializedName(value = "address", alternate = ["Address"]) val address: String? = null,
    @SerializedName(value = "rating", alternate = ["Rating", "average_rating", "AverageRating"]) val rating: Double? = null,
    @SerializedName(value = "imageUrl", alternate = ["image_url", "ImageName", "ImageURL", "photo_url", "PhotoURL"]) val imageUrl: String? = null,
    @SerializedName(value = "Images", alternate = ["images"]) val images: List<String>? = null
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

data class ProductReviewsDto(
    @SerializedName(value = "Summary", alternate = ["summary"]) val summary: ReviewsSummaryDto?,
    @SerializedName(value = "Reviews", alternate = ["reviews"]) val reviews: List<ReviewItemDto>?
)

data class ReviewsSummaryDto(
    @SerializedName(value = "ProductID", alternate = ["product_id", "productId"]) val productId: Int,
    @SerializedName(value = "AverageRating", alternate = ["average_rating", "averageRating"]) val averageRating: Double,
    @SerializedName(value = "RatingsCount", alternate = ["ratings_count", "ratingsCount"]) val ratingsCount: Int,
    @SerializedName(value = "ReviewsCount", alternate = ["reviews_count", "reviewsCount"]) val reviewsCount: Int,
    @SerializedName(value = "Count", alternate = ["count"]) val count: Int
)

data class ReviewItemDto(
    @SerializedName(value = "ID", alternate = ["id"]) val id: Int,
    @SerializedName(value = "UserName", alternate = ["user_name", "userName"]) val userName: String,
    @SerializedName(value = "ProductID", alternate = ["product_id", "productId"]) val productId: Int,
    @SerializedName(value = "ProductTitle", alternate = ["product_title", "productTitle"]) val productTitle: String,
    @SerializedName(value = "Rating", alternate = ["rating"]) val rating: Double,
    @SerializedName(value = "CreatedAt", alternate = ["created_at", "createdAt"]) val createdAt: String,
    @SerializedName(value = "Text", alternate = ["text"]) val text: String,
    @SerializedName(value = "photo_url", alternate = ["user_photo", "avatar_url"]) val photoUrl: String? = null
)

data class SellerQualityDto(
    @SerializedName(value = "seller_name", alternate = ["sellerName", "Name"]) val sellerName: String,
    @SerializedName(value = "orders_count", alternate = ["ordersCount", "total_orders"]) val ordersCount: Int,
    @SerializedName(value = "days_with_ozmade", alternate = ["daysWithOzMade"]) val daysWithOzMade: Int,
    @SerializedName(value = "average_rating", alternate = ["averageRating", "rating", "AverageRating"]) val averageRating: Double,
    @SerializedName(value = "ratings_count", alternate = ["ratingsCount", "RatingsCount"]) val ratingsCount: Int,
    @SerializedName(value = "reviews_count", alternate = ["reviewsCount", "ReviewsCount"]) val reviewsCount: Int,
    @SerializedName(value = "reviews", alternate = ["Reviews"]) val reviews: List<SellerReviewItemDto>
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

data class DeliveryInfoDto(
    @SerializedName(value = "pickup_enabled", alternate = ["pickupEnabled"]) val pickupEnabled: Boolean,
    @SerializedName(value = "pickup_time", alternate = ["pickupTime"]) val pickupTime: String?,
    @SerializedName(value = "pickup_address", alternate = ["pickupAddress"]) val pickupAddress: String?,
    @SerializedName(value = "free_delivery_enabled", alternate = ["freeDeliveryEnabled"]) val freeDeliveryEnabled: Boolean,
    @SerializedName(value = "free_delivery_text", alternate = ["freeDeliveryText"]) val freeDeliveryText: String?,
    @SerializedName(value = "intercity_enabled", alternate = ["intercityEnabled"]) val intercityEnabled: Boolean,
    @SerializedName(value = "delivery_center_lat", alternate = ["deliveryCenterLat"]) val centerLat: Double? = null,
    @SerializedName(value = "delivery_center_lng", alternate = ["deliveryCenterLng"]) val centerLng: Double? = null,
    @SerializedName(value = "delivery_radius_km", alternate = ["deliveryRadiusKm"]) val radiusKm: Double? = null,
    @SerializedName(value = "delivery_center_address", alternate = ["centerAddress"]) val centerAddress: String? = null,

    @SerializedName(value = "buyer_saved_address", alternate = ["buyerSavedAddress"]) val buyerSavedAddress: String? = null,
    @SerializedName(value = "buyer_saved_address_lat", alternate = ["buyerSavedAddressLat"]) val buyerSavedAddressLat: Double? = null,
    @SerializedName(value = "buyer_saved_address_lng", alternate = ["buyerSavedAddressLng"]) val buyerSavedAddressLng: Double? = null
)

data class SellerInfoDto(
    @SerializedName(value = "ID", alternate = ["id"]) val id: Int,
    @SerializedName(value = "Name", alternate = ["name", "seller_name", "SellerName", "store_name", "storeName", "StoreName", "display_name", "DisplayName"]) val name: String?,
    @SerializedName(value = "photo_url", alternate = ["AvatarURL", "avatar_url", "avatarUrl", "ImageName"]) val photoUrl: String? = null,
    @SerializedName(value = "Address", alternate = ["address"]) val address: String?,
    @SerializedName("Rating") val rating: Double? = null,
    @SerializedName(value = "CompletedOrders", alternate = ["completed_orders", "completedOrders", "ordersCount"]) val completedOrders: Int? = null
)

//data class SellerReviewsDto(
//    @SerializedName(value = "Header", alternate = ["header"]) val header: SellerReviewsHeaderDto?,
//    @SerializedName(value = "Reviews", alternate = ["reviews"]) val reviews: List<SellerReviewItemDto>?
//)

data class SellerReviewsHeaderDto(
    @SerializedName(value = "SellerID", alternate = ["seller_id", "sellerId"]) val sellerId: Int,
    @SerializedName(value = "SellerName", alternate = ["seller_name", "sellerName"]) val sellerName: String,
    @SerializedName(value = "ReviewsCount", alternate = ["reviews_count", "reviewsCount"]) val reviewsCount: Int,
    @SerializedName(value = "AverageRating", alternate = ["average_rating", "averageRating"]) val averageRating: Double,
    @SerializedName(value = "RatingsCount", alternate = ["ratings_count", "ratingsCount"]) val ratingsCount: Int
)

data class SellerReviewItemDto(
    @SerializedName(value = "ID", alternate = ["id"]) val id: Int,
    @SerializedName(value = "UserName", alternate = ["user_name", "userName"]) val userName: String,
    @SerializedName(value = "ProductID", alternate = ["product_id", "productId"]) val productId: Int,
    @SerializedName(value = "ProductTitle", alternate = ["product_title", "productTitle"]) val productTitle: String,
    @SerializedName(value = "Rating", alternate = ["rating"]) val rating: Double,
    @SerializedName(value = "CreatedAt", alternate = ["created_at", "createdAt"]) val createdAt: String,
    @SerializedName(value = "Text", alternate = ["text"]) val text: String,
    @SerializedName(value = "photo_url", alternate = ["user_photo", "avatar_url"]) val photoUrl: String? = null
)

data class OrderDto(
    @SerializedName(value = "ID", alternate = ["id"]) val id: Int,
    @SerializedName(value = "Status", alternate = ["status"]) val status: String,
    @SerializedName(value = "CreatedAt", alternate = ["created_at", "createdAt"]) val createdAt: String,
    @SerializedName(value = "ProductID", alternate = ["product_id", "productId"]) val productId: Int,
    @SerializedName(value = "ProductTitle", alternate = ["product_title", "productTitle"]) val productTitle: String? = null,
    @SerializedName(value = "ProductImageUrl", alternate = ["product_image_url", "productImageUrl", "ImageName"]) val productImageUrl: String? = null,
    @SerializedName(value = "Price", alternate = ["price"]) val price: Double? = null,
    @SerializedName(value = "Quantity", alternate = ["quantity"]) val quantity: Int,
    @SerializedName(value = "TotalCost", alternate = ["total_cost", "totalCost"]) val totalCost: Double,
    @SerializedName(value = "SellerID", alternate = ["seller_id", "sellerId"]) val sellerId: Int? = null,
    @SerializedName(value = "SellerName", alternate = ["seller_name", "sellerName"]) val sellerName: String? = null,
    @SerializedName(value = "DeliveryType", alternate = ["delivery_type", "deliveryType"]) val deliveryType: String,
    @SerializedName(value = "PickupAddress", alternate = ["pickup_address", "pickupAddress"]) val pickupAddress: String? = null,
    @SerializedName(value = "PickupTime", alternate = ["pickup_time", "pickupTime"]) val pickupTime: String? = null,
    @SerializedName(value = "ZoneCenterLat", alternate = ["zone_center_lat", "zoneCenterLat", "delivery_center_lat"]) val zoneCenterLat: Double? = null,
    @SerializedName(value = "ZoneCenterLng", alternate = ["zone_center_lng", "zoneCenterLng", "delivery_center_lng"]) val zoneCenterLng: Double? = null,
    @SerializedName(value = "ZoneRadiusKm", alternate = ["zone_radius_km", "zoneRadiusKm", "delivery_radius_km"]) val zoneRadiusKm: Int? = null,
    @SerializedName(value = "ZoneCenterAddress", alternate = ["zone_center_address", "zoneCenterAddress", "delivery_center_address"]) val zoneCenterAddress: String? = null,
    @SerializedName(value = "ShippingAddressText", alternate = ["shipping_address_text", "shippingAddressText"]) val shippingAddressText: String? = null,
    @SerializedName(value = "ShippingLat", alternate = ["shipping_lat", "shippingLat"]) val shippingLat: Double? = null,
    @SerializedName(value = "ShippingLng", alternate = ["shipping_lng", "shippingLng"]) val shippingLng: Double? = null,
    @SerializedName(value = "ShippingComment", alternate = ["shipping_comment", "shippingComment"]) val shippingComment: String? = null,
    @SerializedName(value = "ConfirmCode", alternate = ["confirm_code", "confirmCode"]) val confirmCode: String? = null
)
