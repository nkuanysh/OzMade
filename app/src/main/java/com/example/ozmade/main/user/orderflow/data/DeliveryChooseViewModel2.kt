package com.example.ozmade.main.user.orderflow.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.userHome.details.ProductDetailsUi
import com.example.ozmade.main.userHome.details.ProductRepository
import com.example.ozmade.network.model.CreateOrderRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryChooseViewModel2 @Inject constructor(
    private val productRepo: ProductRepository,
    private val orderRepo: OrderFlowRepository
) : ViewModel() {

    sealed class UiState {
        data object Loading : UiState()
        data class Error(val message: String) : UiState()
        data class Data(
            val product: ProductDetailsUi,
            val saving: Boolean = false,
            val actionError: String? = null
        ) : UiState()
    }

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    fun load(productId: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching { productRepo.getProductDetails(productId.toString()) }
                .onSuccess { _state.value = UiState.Data(product = it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun createOrder(
        productId: Int,
        quantity: Int,
        deliveryType: String,
        shippingAddressText: String?,
        onSuccess: () -> Unit
    ) {
        val current = _state.value
        if (current !is UiState.Data) return

        viewModelScope.launch {
            _state.value = current.copy(saving = true, actionError = null)

            runCatching {
                orderRepo.create(
                    CreateOrderRequest(
                        productId = productId,
                        quantity = quantity,
                        deliveryType = deliveryType,
                        shippingAddressText = shippingAddressText
                    )
                )
            }.onSuccess {
                onSuccess()
            }.onFailure {
                val cur = _state.value
                if (cur is UiState.Data) _state.value = cur.copy(saving = false, actionError = it.message ?: "Ошибка")
                return@launch
            }

            val cur = _state.value
            if (cur is UiState.Data) _state.value = cur.copy(saving = false)
        }
    }
}