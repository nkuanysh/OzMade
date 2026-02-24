package com.example.ozmade.main.seller.quality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.quality.data.SellerQualityRepository
import com.example.ozmade.main.seller.quality.data.SellerQualityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerQualityViewModel @Inject constructor(
    private val repo: SellerQualityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerQualityUiState>(SellerQualityUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load() {
        _uiState.value = SellerQualityUiState.Loading
        viewModelScope.launch {
            runCatching { repo.load() }
                .onSuccess { _uiState.value = SellerQualityUiState.Data(it) }
                .onFailure { _uiState.value = SellerQualityUiState.Error(it.message ?: "Ошибка") }
        }
    }
}