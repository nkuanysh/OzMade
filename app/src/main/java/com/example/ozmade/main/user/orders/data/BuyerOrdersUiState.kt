package com.example.ozmade.main.user.orders.data

import com.example.ozmade.main.orders.data.OrderUi

sealed class BuyerOrdersUiState {
    data object Loading : BuyerOrdersUiState()
    data class Error(val message: String) : BuyerOrdersUiState()
    data class Data(val orders: List<OrderUi>) : BuyerOrdersUiState()
}