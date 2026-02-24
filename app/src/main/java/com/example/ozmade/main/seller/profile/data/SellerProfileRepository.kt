package com.example.ozmade.main.seller.profile.data

interface SellerProfileRepository {
    suspend fun getSellerProfile(): SellerProfileUi
}