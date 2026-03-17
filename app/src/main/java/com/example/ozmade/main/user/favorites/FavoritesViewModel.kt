package com.example.ozmade.main.user.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.userHome.details.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repo: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadFavorites() {
        _uiState.value = FavoritesUiState.Loading

        viewModelScope.launch {
            runCatching {
                repo.getFavorites()
            }.onSuccess { items ->
                _uiState.value = FavoritesUiState.Data(items)
            }.onFailure { e ->
                _uiState.value = FavoritesUiState.Error(
                    e.message ?: "Не удалось загрузить избранное"
                )
            }
        }
    }

    fun removeFromFavorites(productId: Int) {
        viewModelScope.launch {
            runCatching {
                repo.toggleLike(productId)
            }.onSuccess { isLikedNow ->
                val currentState = _uiState.value
                if (currentState is FavoritesUiState.Data) {
                    if (!isLikedNow) {
                        _uiState.value = currentState.copy(
                            items = currentState.items.filterNot { it.id == productId }
                        )
                    } else {
                        loadFavorites()
                    }
                }
            }
        }
    }
}