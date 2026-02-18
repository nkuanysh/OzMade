package com.example.ozmade.network.api

import com.example.ozmade.network.dto.ReviewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewsApi {

    // Вариант 1 (рекомендуемый): /products/{id}/reviews
    @GET("/products/{id}/reviews")
    suspend fun getProductReviews(
        @Path("id") productId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ReviewsResponseDto
}
