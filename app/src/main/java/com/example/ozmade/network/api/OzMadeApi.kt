package com.example.ozmade.network.api

import com.example.ozmade.network.model.*
import retrofit2.Response
import retrofit2.http.*

interface OzMadeApi {

    @GET("products")
    suspend fun getProducts(
        @Query("type") type: String? = null,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 10
    ): Response<List<ProductDto>>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String? = null,
        @Query("type") type: String? = null,
        @Query("category") category: String? = null,
        @Query("min_cost") minCost: Double? = null,
        @Query("max_cost") maxCost: Double? = null,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 20
    ): Response<List<ProductDto>>

    @GET("ads")
    suspend fun getAds(): Response<List<AdDto>>

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") id: Int): Response<ProductDetailsDto>

    @POST("products/{id}/view")
    suspend fun incrementProductView(@Path("id") id: Int): Response<Unit>

    @GET("products/trending")
    suspend fun getTrendingProducts(): Response<List<ProductDto>>

    @POST("auth/sync")
    suspend fun syncUser(
        @Body request: SyncRequest = SyncRequest()
    ): Response<AuthSyncResponse>

    @POST("products/{id}/comments")
    suspend fun postComment(
        @Path("id") id: Int,
        @Body request: CommentRequest
    ): Response<CommentDto>

    @POST("products/{id}/report")
    suspend fun reportProduct(
        @Path("id") id: Int,
        @Body request: ReportRequest
    ): Response<Unit>

    @GET("profile")
    suspend fun getProfile(): Response<ProfileDto>

    @PATCH("profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<ProfileDto>

    @POST("profile/favorites/{id}")
    suspend fun toggleFavorite(
        @Path("id") id: Int
    ): Response<FavoriteStatusResponse>

    @GET("profile/favorites")
    suspend fun getFavorites(): Response<List<ProductDto>>

    @GET("profile/orders")
    suspend fun getOrders(): Response<List<OrderDto>>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Path("id") orderId: Int
    ): Response<MessageResponse>

    @POST("orders/{id}/received")
    suspend fun markOrderReceived(
        @Path("id") orderId: Int
    ): Response<MessageResponse>

    @POST("seller/register")
    suspend fun registerSeller(
        @Body request: SellerRegistrationRequestDto
    ): Response<MessageResponse>

    @GET("seller/upload-id-url")
    suspend fun getUploadIdUrl(): Response<UploadUrlResponse>

    @GET("seller/upload-product-photo-url")
    suspend fun getUploadProductPhotoUrl(
        @Query("content_type") contentType: String
    ): Response<UploadUrlResponse>

    @GET("seller/products")
    suspend fun getSellerProducts(): Response<List<ProductDto>>

    @POST("seller/products")
    suspend fun createProduct(
        @Body request: ProductCreateRequest
    ): Response<ProductDto>

    @PUT("seller/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body request: ProductRequest
    ): Response<ProductDto>

    @PATCH("seller/products/{id}")
    suspend fun patchProduct(
        @Path("id") id: Int,
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): Response<ProductDto>

    @DELETE("seller/products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<MessageResponse>

    @GET("products/{id}/reviews")
    suspend fun getProductReviews(@Path("id") productId: Int): Response<ProductReviewsDto>

    @GET("seller/profile")
    suspend fun getSellerProfile(): Response<SellerProfileDto>

    @PATCH("seller/profile")
    suspend fun updateSellerProfile(
        @Body request: UpdateSellerProfileRequest
    ): Response<MessageResponse>

    @GET("sellers/{id}")
    suspend fun getSellerPage(@Path("id") sellerId: Int): Response<SellerPageDto>

    @GET("sellers/{id}/reviews")
    suspend fun getSellerReviews(@Path("id") sellerId: Int): Response<SellerPageDto>

    // Added to support seller quality view if seller/quality is 404
    @GET("seller/{id}/review")
    suspend fun getSellerReviewLegacy(@Path("id") id: Int): Response<SellerPageDto>

    @GET("seller/quality")
    suspend fun getSellerQuality(): Response<SellerQualityDto>

    @GET("seller/delivery")
    suspend fun getSellerDelivery(): Response<SellerDeliveryDto>

    @PATCH("seller/delivery")
    suspend fun updateSellerDelivery(
        @Body request: UpdateSellerDeliveryRequest
    ): Response<SellerDeliveryDto>

    @GET("products/recommendations")
    suspend fun getRecommendations(
        @Query("limit") limit: Int? = 20
    ): Response<List<ProductDto>>

    // buyer
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<OrderDto>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrderBuyer(@Path("id") id: Int): Response<Unit>

    @POST("orders/{id}/received")
    suspend fun buyerReceived(@Path("id") id: Int): Response<Unit>

    // seller
    @GET("seller/orders")
    suspend fun getSellerOrders(): Response<List<OrderDto>>

    @POST("seller/orders/{id}/confirm")
    suspend fun confirmOrder(@Path("id") id: Int): Response<Unit>

    @POST("seller/orders/{id}/cancel")
    suspend fun cancelOrderSeller(@Path("id") id: Int): Response<Unit>

    @POST("seller/orders/{id}/ready_or_shipped")
    suspend fun readyOrShipped(
        @Path("id") id: Int,
        @Body request: ReadyOrShippedRequest
    ): Response<Unit>

    @POST("seller/orders/{id}/complete")
    suspend fun completeOrder(
        @Path("id") id: Int,
        @Body request: CompleteOrderRequest
    ): Response<Unit>

    @POST("chats")
    suspend fun createBuyerChat(
        @Body request: CreateChatRequest
    ): Response<ChatDto>

    @GET("chats")
    suspend fun getBuyerChats(
        @Query("role") role: String = "buyer"
    ): Response<List<ChatDto>>

    @DELETE("chats/{chat_id}")
    suspend fun deleteChat(
        @Path("chat_id") chatId: Int
    ): Response<Unit>

    @GET("chats/{chat_id}/messages")
    suspend fun getBuyerChatMessages(
        @Path("chat_id") chatId: Int
    ): Response<List<MessageDto>>

    @POST("chats/{chat_id}/messages")
    suspend fun sendBuyerChatMessage(
        @Path("chat_id") chatId: Int,
        @Body request: ChatSendMessageRequest
    ): Response<MessageDto>

    @GET("chats")
    suspend fun getSellerChats(
        @Query("role") role: String = "seller"
    ): Response<List<ChatDto>>

    @GET("seller/chats/{chat_id}/messages")
    suspend fun getSellerChatMessages(
        @Path("chat_id") chatId: Int
    ): Response<List<MessageDto>>

    @POST("seller/chats/{chat_id}/messages")
    suspend fun sendSellerChatMessage(
        @Path("chat_id") chatId: Int,
        @Body request: ChatSendMessageRequest
    ): Response<MessageDto>

    @PATCH("profile/fcm-token")
    suspend fun updateFCMToken(
        @Body request: FCMTokenRequest
    ): Response<MessageResponse>

    @GET("notifications")
    suspend fun getNotifications(): Response<List<NotificationDto>>

    @POST("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Int): Response<MessageResponse>

    @POST("notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<MessageResponse>
}
