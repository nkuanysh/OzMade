package com.example.ozmade.main.home

interface HomeRepository {
    suspend fun getHome(): HomeResponse
}

data class HomeResponse(
    val ads: List<AdBanner>,
    val categories: List<Category>,
    val products: List<Product>
)
