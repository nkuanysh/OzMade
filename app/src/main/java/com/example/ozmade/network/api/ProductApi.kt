package com.example.ozmade.network.api

import com.example.ozmade.network.dto.ProductDetailsDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {

    @GET("/products/{id}")
    suspend fun getProductDetails(
        @Path("id") id: String
    ): ProductDetailsDto
}
