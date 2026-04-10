package com.example.ozmade.main.userHome.details

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

    fun load(productId: Int) {
        viewModelScope.launch {
            _uiState.value = ProductDetailsUiState.Loading
            try {
                val product = repo.getProductDetails(productId)
                _uiState.value = ProductDetailsUiState.Data(
                    product = product,
                    liked = repo.isLiked(productId)
                )
            } catch (e: Exception) {
                _uiState.value = ProductDetailsUiState.Error(
                    e.message ?: "Ошибка загрузки товара"
                )
            }
        }
    }

    fun toggleLike() {
        val state = _uiState.value
        if (state !is ProductDetailsUiState.Data) return

        viewModelScope.launch {
            runCatching {
                repo.toggleLike(state.product.id)
            }.onSuccess { newLiked ->
                _uiState.value = state.copy(liked = newLiked)
            }.onFailure {
                // если хочешь, потом можно показать Snackbar
                _uiState.value = state
            }
        }
    }
}
