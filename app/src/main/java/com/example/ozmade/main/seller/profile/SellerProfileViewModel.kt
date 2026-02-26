package com.example.ozmade.main.seller.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.profile.data.SellerProfileRepository
import com.example.ozmade.main.seller.profile.data.SellerProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerProfileViewModel @Inject constructor(
    private val repo: SellerProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerProfileUiState>(SellerProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load() {
        _uiState.value = SellerProfileUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getSellerProfile() }
                .onSuccess { _uiState.value = SellerProfileUiState.Data(it) }
                .onFailure { _uiState.value = SellerProfileUiState.Error(it.message ?: "Ошибка") }
        }
    }
}