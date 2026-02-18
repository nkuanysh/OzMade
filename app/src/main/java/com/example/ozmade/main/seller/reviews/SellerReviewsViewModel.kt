package com.example.ozmade.main.seller.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerReviewsViewModel @Inject constructor(
    private val repo: SellerReviewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerReviewsUiState>(SellerReviewsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load(sellerId: String) {
        _uiState.value = SellerReviewsUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getSellerReviews(sellerId) }
                .onSuccess { resp ->
                    _uiState.value = SellerReviewsUiState.Data(
                        header = resp.header,
                        reviews = resp.reviews
                    )
                }
                .onFailure {
                    _uiState.value = SellerReviewsUiState.Error(it.message ?: "Ошибка загрузки отзывов")
                }
        }
    }
}
