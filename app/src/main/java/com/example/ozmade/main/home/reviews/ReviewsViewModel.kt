package com.example.ozmade.main.home.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val repo: ReviewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewsUiState>(ReviewsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load(productId: String) {
        _uiState.value = ReviewsUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getReviews(productId) }
                .onSuccess { resp ->
                    _uiState.value = ReviewsUiState.Data(
                        titleCount = resp.summary.reviewsCount,
                        summary = resp.summary,
                        reviews = resp.reviews
                    )
                }
                .onFailure {
                    _uiState.value = ReviewsUiState.Error(it.message ?: "Ошибка загрузки отзывов")
                }
        }
    }
}
