package com.example.ozmade.main.userHome.seller
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    private val repo: SellerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerUiState>(SellerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load(sellerId: String) {
        _uiState.value = SellerUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getSellerPage(sellerId) }
                .onSuccess { resp ->
                    // Сразу подтянем лайки
                    val likedIds = resp.products
                        .map { it.id }
                        .filter { repo.isLiked(it) }
                        .toSet()

                    _uiState.value = SellerUiState.Data(
                        seller = resp.seller,
                        products = resp.products,
                        likedIds = likedIds
                    )
                }
                .onFailure {
                    _uiState.value = SellerUiState.Error(it.message ?: "Ошибка загрузки продавца")
                }
        }
    }

    fun toggleLike(productId: String) {
        val state = _uiState.value
        if (state !is SellerUiState.Data) return

        viewModelScope.launch {
            val newLiked = repo.toggleLike(productId)
            val newSet = state.likedIds.toMutableSet()
            if (newLiked) newSet.add(productId) else newSet.remove(productId)
            _uiState.value = state.copy(likedIds = newSet)
        }
    }
}
