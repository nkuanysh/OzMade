package com.example.ozmade.main.seller.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.data.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SellerGateState {
    object Idle : SellerGateState()
    object Loading : SellerGateState()
    object OpenSellerHome : SellerGateState()
    object OpenOnboarding : SellerGateState()
    data class Error(val message: String) : SellerGateState()
}

@HiltViewModel
class SellerGateViewModel @Inject constructor(
    private val repo: SellerRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SellerGateState>(SellerGateState.Idle)
    val state = _state.asStateFlow()

    fun checkAndRoute() {
        _state.value = SellerGateState.Loading
        viewModelScope.launch {
            runCatching { repo.sellerProfileExists() }
                .onSuccess { exists ->
                    _state.value = if (exists) SellerGateState.OpenSellerHome else SellerGateState.OpenOnboarding
                }
                .onFailure { e ->
                    _state.value = SellerGateState.Error(e.message ?: "Ошибка проверки продавца")
                }
        }
    }

    fun reset() { _state.value = SellerGateState.Idle
    }
}