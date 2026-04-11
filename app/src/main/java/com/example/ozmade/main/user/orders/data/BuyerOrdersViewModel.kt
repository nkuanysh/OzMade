package com.example.ozmade.main.user.orders.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.orders.data.OrderStatus
import com.example.ozmade.network.model.CreateOrderRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyerOrdersViewModel @Inject constructor(
    private val repo: BuyerOrdersRepository,
    private val productRepo: com.example.ozmade.main.userHome.details.ProductRepository
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

    fun postReview(productId: Int, rating: Int, text: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            Log.d("BuyerOrdersViewModel", "Posting review: prodId=$productId, rating=$rating, text=$text")
            
            // Critical fix: Ensure rating is not 0 and text is not empty
            if (rating < 1 || text.isBlank()) {
                Log.e("BuyerOrdersViewModel", "Invalid rating ($rating) or empty text")
                return@launch
            }

            productRepo.postComment(productId, rating, text)
                .onSuccess { 
                    Log.d("BuyerOrdersViewModel", "Review posted successfully")
                    onComplete() 
                }
                .onFailure { e ->
                    Log.e("BuyerOrdersViewModel", "Failed to post review. Error message: ${e.message}")
                    e.printStackTrace()
                }
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
