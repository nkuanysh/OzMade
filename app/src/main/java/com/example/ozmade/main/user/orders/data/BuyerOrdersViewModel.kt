package com.example.ozmade.main.user.orders.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.orders.data.OrderStatus
import com.example.ozmade.main.orders.data.OrderUi
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
                .onSuccess { orders ->
                    val sorted = orders.sortedWith(
                        compareBy<OrderUi> { getStatusPriority(it.status) }
                            .thenByDescending { it.id }
                    )
                    _ui.value = BuyerOrdersUiState.Data(sorted)
                }
                .onFailure { _ui.value = BuyerOrdersUiState.Error(it.message ?: "Ошибка") }
        }
    }

    private fun getStatusPriority(status: String): Int {
        return when (status) {
            OrderStatus.PENDING_SELLER -> 0
            OrderStatus.CONFIRMED -> 1
            OrderStatus.READY_OR_SHIPPED -> 2
            OrderStatus.COMPLETED -> 3
            else -> 4 // CANCELLED, EXPIRED
        }
    }

    fun postReview(orderId: Int, productId: Int, rating: Double, text: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            Log.d("BuyerOrdersViewModel", "Posting review: orderId=$orderId, prodId=$productId, rating=$rating, text=$text")
            
            if (rating < 1.0 || text.isBlank()) {
                Log.e("BuyerOrdersViewModel", "Invalid rating ($rating) or empty text")
                return@launch
            }

            productRepo.postComment(productId, rating, text, orderId)
                .onSuccess { 
                    Log.d("BuyerOrdersViewModel", "Review posted successfully")
                    
                    // Обновляем локальное состояние, чтобы кнопка сразу исчезла
                    val current = _ui.value
                    if (current is BuyerOrdersUiState.Data) {
                        val updated = current.orders.map { 
                            if (it.id == orderId) it.copy(isReviewed = true) else it
                        }
                        _ui.value = BuyerOrdersUiState.Data(updated)
                    }

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

    fun findById(id: Int): OrderUi? {
        val st = _ui.value
        return (st as? BuyerOrdersUiState.Data)?.orders?.firstOrNull { it.id == id }
    }
}
