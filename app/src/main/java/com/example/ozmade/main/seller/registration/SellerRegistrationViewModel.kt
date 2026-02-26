package com.example.ozmade.main.seller.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.data.SellerRepository
import com.example.ozmade.network.model.SellerRegistrationRequestDto
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

    fun submit() {
        _state.value = SellerRegState.Loading
        viewModelScope.launch {
            repo.registerSeller()
                .onSuccess {
                    _state.value = SellerRegState.Success
                }
                .onFailure {
                    _state.value = SellerRegState.Error(it.message ?: "Ошибка регистрации")
                }
        }
    }

    fun reset() { _state.value = SellerRegState.Idle }
}