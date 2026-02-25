package com.example.ozmade.main.user.orders.data

import com.example.ozmade.main.orders.data.OrderUi
import com.example.ozmade.network.api.OzMadeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealBuyerOrdersRepository @Inject constructor(
    private val api: OzMadeApi
) : BuyerOrdersRepository {

    override suspend fun getMyOrders(): List<OrderUi> = withContext(Dispatchers.IO) {
        val resp = api.getOrders()
        if (!resp.isSuccessful) error("Не удалось загрузить заказы (${resp.code()})")
        val list = resp.body() ?: emptyList()

        list.map { dto ->
            OrderUi(
                id = dto.id,
                status = dto.status,
                createdAt = dto.createdAt,

                productId = dto.productId,
                productTitle = dto.productTitle ?: "Товар",
                productImageUrl = dto.productImageUrl,
                price = dto.price ?: 0.0,
                quantity = dto.quantity,
                totalCost = dto.totalCost,

                sellerName = dto.sellerName,

                deliveryType = dto.deliveryType,
                pickupAddress = dto.pickupAddress,
                pickupTime = dto.pickupTime,
                zoneCenterAddress = dto.zoneCenterAddress,
                zoneRadiusKm = dto.zoneRadiusKm,
                shippingAddressText = dto.shippingAddressText,

                confirmCode = dto.confirmCode
            )
        }
    }

    override suspend fun cancelOrder(id: Int) = withContext(Dispatchers.IO) {
        val resp = api.cancelOrderBuyer(id)
        if (!resp.isSuccessful) error("Не удалось отменить заказ (${resp.code()})")
    }

    override suspend fun received(id: Int) = withContext(Dispatchers.IO) {
        val resp = api.buyerReceived(id)
        if (!resp.isSuccessful) error("Не удалось подтвердить получение (${resp.code()})")
    }
}