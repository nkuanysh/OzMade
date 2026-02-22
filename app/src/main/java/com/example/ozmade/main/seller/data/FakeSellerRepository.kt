package com.example.ozmade.main.seller.data

import com.example.ozmade.main.seller.products.SellerProductStatus
import com.example.ozmade.main.seller.products.SellerProductUi
import com.example.ozmade.network.dto.SellerRegistrationRequestDto
import com.example.ozmade.network.dto.SellerRegistrationResponseDto
import kotlinx.coroutines.delay
import java.util.concurrent.CopyOnWriteArrayList
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
    private val items = CopyOnWriteArrayList(
        listOf(
            SellerProductUi(
                id = "p1",
                title = "Домашний мёд 1л",
                price = 4500,
                imageUrl = null,
                status = SellerProductStatus.ON_SALE
            ),
            SellerProductUi(
                id = "p2",
                title = "Картина акрил 30x40",
                price = 12000,
                imageUrl = null,
                status = SellerProductStatus.PENDING_MODERATION
            ),
            SellerProductUi(
                id = "p3",
                title = "Свеча ручной работы",
                price = 2500,
                imageUrl = null,
                status = SellerProductStatus.OFF_SALE
            )
        )
    )

    override suspend fun getMyProducts(): List<SellerProductUi> {
        delay(250) // имитация сети
        return items.toList()
    }

    override suspend fun updateProductPrice(productId: String, newPrice: Int) {
        delay(150)
        val idx = items.indexOfFirst { it.id == productId }
        if (idx >= 0) items[idx] = items[idx].copy(price = newPrice)
    }

    override suspend fun toggleProductSaleState(productId: String) {
        delay(150)
        val idx = items.indexOfFirst { it.id == productId }
        if (idx < 0) return

        val current = items[idx]
        val next = when (current.status) {
            SellerProductStatus.PENDING_MODERATION -> SellerProductStatus.OFF_SALE // “Остановить проверку”
            SellerProductStatus.OFF_SALE -> SellerProductStatus.ON_SALE           // “Выставить на продажу”
            SellerProductStatus.ON_SALE -> SellerProductStatus.OFF_SALE           // “Снять с продажи”
        }
        items[idx] = current.copy(status = next)
    }

    override suspend fun deleteProduct(productId: String) {
        delay(150)
        items.removeIf { it.id == productId }
    }

}