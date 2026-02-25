package com.example.ozmade.main.user.orders.data

import com.example.ozmade.main.orders.data.OrderUi

interface BuyerOrdersRepository {
    suspend fun getMyOrders(): List<OrderUi>
    suspend fun cancelOrder(id: Int)
    suspend fun received(id: Int)
}