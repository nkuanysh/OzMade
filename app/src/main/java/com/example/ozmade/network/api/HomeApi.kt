package com.example.ozmade.network.api

import com.example.ozmade.network.dto.AdDto
import com.example.ozmade.network.dto.CategoryDto
import com.example.ozmade.network.dto.ProductDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {

    // Баннеры рекламы
    @GET("/ads")
    suspend fun getAds(): List<AdDto>

    // Категории
    @GET("/categories")
    suspend fun getCategories(): List<CategoryDto>

    // Товары. Можно сделать параметры:
    // q - поиск, categoryId - фильтр, city - фильтр, page/limit - пагинация
    @GET("/products")
    suspend fun getProducts(
        @Query("q") q: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("city") city: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): List<ProductDto>
}
