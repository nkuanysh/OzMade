package com.example.ozmade.main.user.orderflow.data

import com.example.ozmade.network.model.CreateOrderRequest
import com.example.ozmade.network.model.OrderDto

interface OrderFlowRepository {
    suspend fun create(request: CreateOrderRequest): OrderDto
}