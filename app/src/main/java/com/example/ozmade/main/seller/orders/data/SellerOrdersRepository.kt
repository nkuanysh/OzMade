package com.example.ozmade.main.seller.orders.data

import com.example.ozmade.main.orders.data.OrderUi

interface SellerOrdersRepository {
    suspend fun getOrders(): List<OrderUi>
    suspend fun confirm(id: Int)
    suspend fun cancel(id: Int)
    suspend fun readyOrShipped(id: Int, comment: String?)
    suspend fun completeWithCode(id: Int, code: String)
}