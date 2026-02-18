package com.example.ozmade.network.api

import com.example.ozmade.network.dto.SellerPageDto
import retrofit2.http.GET
import retrofit2.http.Path

interface SellerApi {

    @GET("/sellers/{id}")
    suspend fun getSellerPage(@Path("id") sellerId: String): SellerPageDto
}
