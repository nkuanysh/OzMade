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

    @GET("products/{id}")
    suspend fun getProductDetails(
        @Path("id") id: Int
    ): Response<ProductDetailsDto>

    @POST("products/{id}/view")
    suspend fun incrementProductView(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("products/trending")
    suspend fun getTrendingProducts(): Response<List<ProductDto>>

    @POST("auth/sync")
    suspend fun syncUser(): Response<AuthSyncResponse>

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

    @GET("profile/favorites")
    suspend fun getFavoritesResponse(): Response<List<ProductDto>>

    @GET("profile/orders")
    suspend fun getOrders(): Response<List<OrderDto>>
    @POST("seller/register")
    suspend fun registerSeller(): Response<MessageResponse>

    @GET("seller/upload-id-url")
    suspend fun getUploadIdUrl(): Response<UploadUrlResponse>

    @GET("seller/products")
    suspend fun getSellerProducts(): Response<List<ProductDto>>

    @POST("seller/products")
    suspend fun createProduct(
        @Body request: ProductRequest
    ): Response<ProductDto>

    @PUT("seller/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body request: ProductRequest
    ): Response<ProductDto>

    @DELETE("seller/products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<MessageResponse>

    @GET("ads")
    suspend fun getAds(): Response<List<AdDto>>

    @GET("products/{id}/reviews")
    suspend fun getProductReviews(@Path("id") productId: String): ProductReviewsDto

    @GET("products/{id}")
    suspend fun getProductDetailsFull(@Path("id") id: String): ProductDetailsFullDto

    @GET("profile/chats")
    suspend fun getBuyerChats(): Response<List<ChatThreadDto>>

    @GET("profile/chats/{thread_id}/messages")
    suspend fun getBuyerChatMessages(
        @Path("thread_id") threadId: String
    ): Response<List<ChatMessageItemDto>>

    @PATCH("seller/products/{id}")
    suspend fun patchProduct(
        @Path("id") id: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): Response<ProductDto>
    @POST("profile/chats/ensure")
    suspend fun ensureBuyerChat(
        @Body request: EnsureThreadRequest
    ): Response<EnsureThreadResponse>

    @POST("profile/chats/{thread_id}/messages")
    suspend fun sendBuyerChatMessage(
        @Path("thread_id") threadId: String,
        @Body request: SendMessageRequest
    ): Response<Unit>

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @GET("seller/profile")
    suspend fun getSellerProfile(): Response<SellerProfileDto>

    @PATCH("seller/profile")
    suspend fun updateSellerProfile(
        @Body request: UpdateSellerProfileRequest
    ): Response<MessageResponse>

    @GET("seller/chats")
    suspend fun getSellerChats(): Response<List<ChatDto>>

    @GET("seller/chats/{chat_id}/messages")
    suspend fun getChatMessages(
        @Path("chat_id") chatId: Int
    ): Response<List<MessageDto>>

    @POST("seller/chats/{chat_id}/messages")
    suspend fun sendSellerChatMessage(
        @Path("chat_id") chatId: Int,
        @Body request: SendMessageRequest
    ): Response<Unit>

    @GET("sellers/{id}")
    suspend fun getSellerPage(@Path("id") sellerId: String): SellerPageDto

    @GET("sellers/{id}/reviews")
    suspend fun getSellerReviews(@Path("id") sellerId: String): SellerReviewsDto
}