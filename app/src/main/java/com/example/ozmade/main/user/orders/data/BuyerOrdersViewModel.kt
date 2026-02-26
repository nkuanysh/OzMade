package com.example.ozmade.main.user.orders.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.orders.data.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyerOrdersViewModel @Inject constructor(
    private val repo: BuyerOrdersRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<BuyerOrdersUiState>(BuyerOrdersUiState.Loading)
    val ui: StateFlow<BuyerOrdersUiState> = _ui

    fun load() {
        viewModelScope.launch {
            _ui.value = BuyerOrdersUiState.Loading
            runCatching { repo.getMyOrders() }
                .onSuccess { _ui.value = BuyerOrdersUiState.Data(it.sortedByDescending { o -> o.id }) }
                .onFailure { _ui.value = BuyerOrdersUiState.Error(it.message ?: "Ошибка") }
        }
    }

    fun cancel(orderId: Int) {
        viewModelScope.launch {
            runCatching { repo.cancelOrder(orderId) }.onSuccess { load() }
        }
    }

    fun received(orderId: Int) {
        viewModelScope.launch {
            runCatching { repo.received(orderId) }.onSuccess { load() }
        }
    }

    fun findById(id: Int): com.example.ozmade.main.orders.data.OrderUi? {
        val st = _ui.value
        return (st as? BuyerOrdersUiState.Data)?.orders?.firstOrNull { it.id == id }
    }
}