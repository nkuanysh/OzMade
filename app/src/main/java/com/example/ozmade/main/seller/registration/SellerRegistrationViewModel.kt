package com.example.ozmade.main.seller.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.data.SellerRepository
import com.example.ozmade.network.dto.SellerRegistrationRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SellerRegState {
    object Idle : SellerRegState()
    object Loading : SellerRegState()
    object Success : SellerRegState()
    data class Error(val message: String) : SellerRegState()
}

@HiltViewModel
class SellerRegistrationViewModel @Inject constructor(
    private val repo: SellerRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SellerRegState>(SellerRegState.Idle)
    val state = _state.asStateFlow()

    fun submit(request: SellerRegistrationRequestDto) {
        _state.value = SellerRegState.Loading
        viewModelScope.launch {
            try {
                repo.registerSeller(request)
                _state.value = SellerRegState.Success
            } catch (e: Exception) {
                _state.value = SellerRegState.Error(e.message ?: "Ошибка регистрации")
            }
        }
    }

    fun reset() { _state.value = SellerRegState.Idle }
}