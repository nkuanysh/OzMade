package com.example.ozmade.main.seller.orders.data

import com.example.ozmade.main.orders.data.OrderUi
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.CompleteOrderRequest
import com.example.ozmade.network.model.ReadyOrShippedRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealSellerOrdersRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerOrdersRepository {

    override suspend fun getOrders(): List<OrderUi> = withContext(Dispatchers.IO) {
        val resp = api.getSellerOrders()
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

    override suspend fun confirm(id: Int) = withContext(Dispatchers.IO) {
        val resp = api.confirmOrder(id)
        if (!resp.isSuccessful) error("Не удалось подтвердить (${resp.code()})")
    }

    override suspend fun cancel(id: Int) = withContext(Dispatchers.IO) {
        val resp = api.cancelOrderSeller(id)
        if (!resp.isSuccessful) error("Не удалось отменить (${resp.code()})")
    }

    override suspend fun readyOrShipped(id: Int, comment: String?) = withContext(Dispatchers.IO) {
        val resp = api.readyOrShipped(id, ReadyOrShippedRequest(comment))
        if (!resp.isSuccessful) error("Не удалось обновить статус (${resp.code()})")
    }

    override suspend fun completeWithCode(id: Int, code: String) = withContext(Dispatchers.IO) {
        val resp = api.completeOrder(id, CompleteOrderRequest(code))
        if (!resp.isSuccessful) error("Неверный код или ошибка (${resp.code()})")
    }
}