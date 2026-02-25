package com.example.ozmade.main.seller.orders.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.orders.data.OrderUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerOrdersViewModel @Inject constructor(
    private val repo: SellerOrdersRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<SellerOrdersUiState>(SellerOrdersUiState.Loading)
    val ui: StateFlow<SellerOrdersUiState> = _ui

    fun load() {
        viewModelScope.launch {
            _ui.value = SellerOrdersUiState.Loading
            runCatching { repo.getOrders() }
                .onSuccess { _ui.value = SellerOrdersUiState.Data(it.sortedByDescending { o -> o.id }) }
                .onFailure { _ui.value = SellerOrdersUiState.Error(it.message ?: "Ошибка") }
        }
    }

    fun confirm(id: Int) = viewModelScope.launch { runCatching { repo.confirm(id) }.onSuccess { load() } }
    fun cancel(id: Int) = viewModelScope.launch { runCatching { repo.cancel(id) }.onSuccess { load() } }
    fun shipped(id: Int, comment: String?) = viewModelScope.launch { runCatching { repo.readyOrShipped(id, comment) }.onSuccess { load() } }
    fun complete(id: Int, code: String) = viewModelScope.launch { runCatching { repo.completeWithCode(id, code) }.onSuccess { load() } }

    fun findById(id: Int): OrderUi? {
        val st = _ui.value
        return (st as? SellerOrdersUiState.Data)?.orders?.firstOrNull { it.id == id }
    }
}