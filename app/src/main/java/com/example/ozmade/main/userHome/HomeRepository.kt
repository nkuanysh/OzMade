package com.example.ozmade.main.userHome

interface HomeRepository {
    suspend fun getHome(): HomeResponse
}
