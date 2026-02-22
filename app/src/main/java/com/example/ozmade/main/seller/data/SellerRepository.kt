package com.example.ozmade.main.seller.data


import com.example.ozmade.network.dto.SellerRegistrationRequestDto
import com.example.ozmade.network.dto.SellerRegistrationResponseDto

interface SellerRepository {
    suspend fun sellerProfileExists(): Boolean
    suspend fun registerSeller(request: SellerRegistrationRequestDto): SellerRegistrationResponseDto
}