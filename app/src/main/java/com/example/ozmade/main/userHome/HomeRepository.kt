package com.example.ozmade.main.userHome

interface HomeRepository {
    suspend fun getHome(): HomeResponse
    suspend fun toggleFavorite(productId: Int): Boolean

}
