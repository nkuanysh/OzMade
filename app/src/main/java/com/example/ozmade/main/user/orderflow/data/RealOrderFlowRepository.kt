package com.example.ozmade.main.user.orderflow.data

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.CreateOrderRequest
import com.example.ozmade.network.model.OrderDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealOrderFlowRepository @Inject constructor(
    private val api: OzMadeApi
) : OrderFlowRepository {

    override suspend fun create(request: CreateOrderRequest): OrderDto = withContext(Dispatchers.IO) {
        val resp = api.createOrder(request)
        if (!resp.isSuccessful) error("Не удалось создать заказ (${resp.code()})")
        resp.body() ?: error("Пустой ответ")
    }
}