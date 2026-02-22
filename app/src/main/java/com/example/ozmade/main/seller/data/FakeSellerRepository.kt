package com.example.ozmade.main.seller.data

import com.example.ozmade.network.dto.SellerRegistrationRequestDto
import com.example.ozmade.network.dto.SellerRegistrationResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeSellerRepository @Inject constructor(
    private val local: SellerLocalStore
) : SellerRepository {

    override suspend fun sellerProfileExists(): Boolean {
        return local.isSellerRegistered()
    }

    override suspend fun registerSeller(request: SellerRegistrationRequestDto): SellerRegistrationResponseDto {
        // имитируем успешную регистрацию
        local.setSellerRegistered(true)
        return SellerRegistrationResponseDto(
            sellerId = "local-temp",
            status = "approved"
        )
    }
}