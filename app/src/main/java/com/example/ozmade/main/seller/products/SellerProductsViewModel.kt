package com.example.ozmade.main.seller.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.data.SellerRepository
import com.example.ozmade.network.api.OzMadeApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SellerProductsState(
    val loading: Boolean = false,
    val error: String? = null,
    val message: String? = null,


    val query: String = "",
    val filter: SellerProductsFilter = SellerProductsFilter.ALL,

    val all: List<SellerProductUi> = emptyList()
) {
    val filtered: List<SellerProductUi> =
        all
            .asSequence()
            .filter { filter.matches(it.status) }
            .filter { query.isBlank() || it.title.contains(query, ignoreCase = true) }
            .toList()
}

@HiltViewModel
class SellerProductsViewModel @Inject constructor(
    private val repo: SellerRepository,
    private val api: OzMadeApi

) : ViewModel() {

    private val _state = MutableStateFlow(SellerProductsState())
    val state: StateFlow<SellerProductsState> = _state

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { repo.getMyProducts() }
                .onSuccess { list ->
                    _state.update { it.copy(loading = false, all = list) }
                }
                .onFailure { e ->
                    _state.update { it.copy(loading = false, error = e.message ?: "Ошибка загрузки") }
                }
        }
    }

    fun onQueryChange(v: String) {
        _state.update { it.copy(query = v) }
    }

    fun onFilterChange(v: SellerProductsFilter) {
        _state.update { it.copy(filter = v) }
    }

    fun updatePrice(productId: Int, newPrice: Int) {
        viewModelScope.launch {
            runCatching { repo.updateProductPrice(productId, newPrice) }
                .onSuccess { load() }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "Не удалось изменить цену") } }
        }
    }

    fun toggleSale(productId: Int) {
        viewModelScope.launch {
            runCatching { repo.toggleProductSaleState(productId) }
                .onSuccess { load() }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "Не удалось изменить статус") } }
        }
    }

    fun delete(productId: Int) {
        viewModelScope.launch {
            runCatching {
                val resp = api.deleteProduct(productId)
                if (!resp.isSuccessful) error("Ошибка: ${resp.code()} ${resp.message()}")}
                .onSuccess {
                    _state.update { it.copy(message = "Товар удалён") }  // ✅
                    load()
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message ?: "Не удалось удалить товар") }
                }
        }
    }

    fun consumeMessage() {
        _state.update { it.copy(message = null) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}