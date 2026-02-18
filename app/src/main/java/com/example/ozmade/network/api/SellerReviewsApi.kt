package com.example.ozmade.network.api

import com.example.ozmade.network.dto.SellerReviewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface SellerReviewsApi {

    @GET("/sellers/{id}/reviews")
    suspend fun getSellerReviews(@Path("id") sellerId: String): SellerReviewsResponseDto
}
