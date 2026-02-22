package com.example.ozmade.main.seller.data


import com.example.ozmade.main.seller.products.SellerProductUi
import com.example.ozmade.network.dto.SellerRegistrationRequestDto
import com.example.ozmade.network.dto.SellerRegistrationResponseDto

interface SellerRepository {
    suspend fun sellerProfileExists(): Boolean
    suspend fun registerSeller(request: SellerRegistrationRequestDto): SellerRegistrationResponseDto

    suspend fun getMyProducts(): List<SellerProductUi>

    suspend fun updateProductPrice(productId: String, newPrice: Int)

    /**
     * Универсально:
     * - если товар в PENDING, то “Остановить проверку” = OFF_SALE
     * - если товар OFF_SALE -> ON_SALE
     * - если товар ON_SALE -> OFF_SALE
     */
    suspend fun toggleProductSaleState(productId: String)

    suspend fun deleteProduct(productId: String)
}