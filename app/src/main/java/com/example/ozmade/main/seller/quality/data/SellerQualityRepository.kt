package com.example.ozmade.main.seller.quality.data

interface SellerQualityRepository {
    suspend fun load(): SellerQualityUi
}