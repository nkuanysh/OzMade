package com.example.ozmade.main.seller.orders.data

import com.example.ozmade.main.orders.data.OrderUi

sealed class SellerOrdersUiState {
    data object Loading : SellerOrdersUiState()
    data class Error(val message: String) : SellerOrdersUiState()
    data class Data(val orders: List<OrderUi>) : SellerOrdersUiState()
}