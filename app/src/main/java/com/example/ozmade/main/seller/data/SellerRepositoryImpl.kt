package com.example.ozmade.main.seller.data

import com.example.ozmade.main.seller.products.SellerProductUi
import com.example.ozmade.network.dto.SellerRegistrationRequestDto
import com.example.ozmade.network.dto.SellerRegistrationResponseDto
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(
    // потом добавишь api/dao
) : SellerRepository {

    override suspend fun sellerProfileExists(): Boolean {
        // ✅ Временно: всегда false, чтобы всегда открывал onboarding
        // Потом заменишь на запрос: GET /seller/me или /seller/exists
        return false
    }

    override suspend fun registerSeller(request: SellerRegistrationRequestDto): SellerRegistrationResponseDto {
        // ✅ Временно заглушка
        return SellerRegistrationResponseDto(
            sellerId = "temp",
            status = "pending"
        )
    }

    override suspend fun getMyProducts(): List<SellerProductUi> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProductPrice(productId: Int, newPrice: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun toggleProductSaleState(productId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productId: Int) {
        TODO("Not yet implemented")
    }
}