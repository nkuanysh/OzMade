package com.example.ozmade.main.user.orderflow.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.network.model.CreateOrderRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryChooseViewModel @Inject constructor(
    private val repo: OrderFlowRepository
) : ViewModel() {

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createOrder(
        productId: Int,
        quantity: Int,
        deliveryType: String?,
        shippingAddressText: String?,
        onSuccess: () -> Unit
    ) {
        if (deliveryType == null) return

        viewModelScope.launch {
            _saving.value = true
            _error.value = null

            runCatching {
                repo.create(
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
                _error.value = it.message ?: "Ошибка"
            }

            _saving.value = false
        }
    }
}