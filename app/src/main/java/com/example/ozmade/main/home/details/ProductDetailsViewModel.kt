package com.example.ozmade.main.home.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repo: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load(productId: String) {
        _uiState.value = ProductDetailsUiState.Loading
        viewModelScope.launch {
            runCatching {
                val product = repo.getProductDetails(productId)
                val liked = repo.isLiked(productId)
                ProductDetailsUiState.Data(product, liked)
            }
                .onSuccess { _uiState.value = it }
                .onFailure { _uiState.value = ProductDetailsUiState.Error(it.message ?: "Ошибка загрузки товара") }
        }
    }

    fun toggleLike() {
        val state = _uiState.value
        if (state !is ProductDetailsUiState.Data) return

        viewModelScope.launch {
            val newLiked = repo.toggleLike(state.product.id)
            _uiState.value = state.copy(liked = newLiked)
        }
    }
}
