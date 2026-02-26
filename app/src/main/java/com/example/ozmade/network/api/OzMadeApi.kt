package com.example.ozmade.network.api

import com.example.ozmade.network.model.AdDto
import com.example.ozmade.network.model.ProductDetailsDto
import com.example.ozmade.network.model.ProductDto
import com.example.ozmade.network.model.AuthSyncResponse
import com.example.ozmade.network.model.CategoryDto
import com.example.ozmade.network.model.ChatDto
import com.example.ozmade.network.model.ChatSendMessageRequest
import com.example.ozmade.network.model.CommentDto
import com.example.ozmade.network.model.CommentRequest
import com.example.ozmade.network.model.CompleteOrderRequest
import com.example.ozmade.network.model.CreateChatRequest
import com.example.ozmade.network.model.CreateOrderRequest
import com.example.ozmade.network.model.FavoriteStatusResponse
import com.example.ozmade.network.model.MessageDto
import com.example.ozmade.network.model.MessageResponse
import com.example.ozmade.network.model.OrderDto
//import com.example.ozmade.network.model.ProductDetailsFullDto
import com.example.ozmade.network.model.ProductRequest
import com.example.ozmade.network.model.ProductReviewsDto
import com.example.ozmade.network.model.ProfileDto
import com.example.ozmade.network.model.ReportRequest
import com.example.ozmade.network.model.SellerPageDto
import com.example.ozmade.network.model.SellerProfileDto
import com.example.ozmade.network.model.SellerReviewsDto
import com.example.ozmade.network.model.UpdateProfileRequest
import com.example.ozmade.network.model.UpdateSellerProfileRequest
import com.example.ozmade.network.model.UploadUrlResponse
import com.example.ozmade.network.model.EnsureThreadRequest
import com.example.ozmade.network.model.EnsureThreadResponse
import com.example.ozmade.network.model.ProductCreateRequest
import com.example.ozmade.network.model.ReadyOrShippedRequest
import com.example.ozmade.network.model.SellerDeliveryDto
import com.example.ozmade.network.model.SellerQualityDto
import com.example.ozmade.network.model.UpdateSellerDeliveryRequest
import retrofit2.Response
import retrofit2.http.*

interface OzMadeApi {

    @GET("products")
    suspend fun getProducts(
        @Query("type") type: String? = null,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 10
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
        @Body request: ProductCreateRequest
    ): Response<ProductDto>

    @PUT("seller/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body request: ProductRequest
    ): Response<ProductDto>

    @PATCH("seller/products/{id}")
    suspend fun patchProduct(
        @Path("id") id: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): Response<ProductDto>

    @DELETE("seller/products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<MessageResponse>


    @GET("products/{id}/reviews")
    suspend fun getProductReviews(@Path("id") productId: String): ProductReviewsDto


    @POST("profile/chats/ensure")
    suspend fun ensureBuyerChat(
        @Body request: EnsureThreadRequest
    ): Response<EnsureThreadResponse>

//    @POST("profile/chats/{thread_id}/messages")
//    suspend fun sendBuyerChatMessage(
//        @Path("thread_id") threadId: String,
//        @Body request: SendMessageRequest
//    ): Response<Unit>


    @GET("seller/profile")
    suspend fun getSellerProfile(): Response<SellerProfileDto>

    @PATCH("seller/profile")
    suspend fun updateSellerProfile(
        @Body request: UpdateSellerProfileRequest
    ): Response<MessageResponse>

    @GET("sellers/{id}")
    suspend fun getSellerPage(@Path("id") sellerId: String): SellerPageDto

    @GET("sellers/{id}/reviews")
    suspend fun getSellerReviews(@Path("id") sellerId: String): SellerReviewsDto

    @GET("seller/quality")
    suspend fun getSellerQuality(): Response<SellerQualityDto>

    @GET("seller/delivery")
    suspend fun getSellerDelivery(): Response<SellerDeliveryDto>

    @PATCH("seller/delivery")
    suspend fun updateSellerDelivery(
        @Body request: UpdateSellerDeliveryRequest
    ): Response<SellerDeliveryDto>



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

    // 1) создать чат или вернуть существующий + отправить первое сообщение
    @POST("seller/chats")
    suspend fun createChatOrGetExisting(
        @Body request: CreateChatRequest
    ): Response<ChatDto> // по описанию возвращает chat object

    // 2) список чатов покупателя
    @GET("seller/chats")
    suspend fun getBuyerChats(): Response<List<ChatDto>>

    // 3) сообщения (общий для buyer/seller)
    @GET("seller/chats/{chat_id}/messages")
    suspend fun getChatMessages(
        @Path("chat_id") chatId: Int
    ): Response<List<MessageDto>>

    // 4) отправка сообщения (когда chatId уже есть)
    @POST("seller/chats/{chat_id}/messages")
    suspend fun sendChatMessage(
        @Path("chat_id") chatId: Int,
        @Body request: ChatSendMessageRequest
    ): Response<MessageDto> // у тебя в описании 201 Created, лучше вернуть MessageDto

}