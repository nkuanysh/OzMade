package com.example.ozmade.main.userHome.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.delivery.DeliveryEstimateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repo: ProductRepository,
    private val deliveryEstimateRepository: DeliveryEstimateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load(productId: Int) {
        viewModelScope.launch {
            _uiState.value = ProductDetailsUiState.Loading
            try {
                val product = repo.getProductDetails(productId)
                val liked = repo.isLiked(productId)
                _uiState.value = ProductDetailsUiState.Data(
                    product = product,
                    liked = liked,
                    intercityEstimate = initialIntercityState(product)
                )
                estimateIntercityIfPossible(product, liked)
            } catch (e: Exception) {
                _uiState.value = ProductDetailsUiState.Error(
                    e.message ?: "Ошибка загрузки товара"
                )
            }
        }
    }

    private suspend fun estimateIntercityIfPossible(product: ProductDetailsUi, liked: Boolean) {
        val fromCity = product.delivery.sellerPickupCity?.takeIf { it.isNotBlank() } ?: return
        val toCity = product.delivery.buyerSavedCity?.takeIf { it.isNotBlank() } ?: return
        if (!product.delivery.intercityEnabled || fromCity.equals(toCity, ignoreCase = true)) return

        _uiState.value = ProductDetailsUiState.Data(
            product = product,
            liked = liked,
            intercityEstimate = ProductIntercityEstimateUiState.Loading
        )

        deliveryEstimateRepository.estimateIntercityDelivery(
            fromCity = fromCity,
            toCity = toCity,
            weightGrams = product.packageInfo.weightGrams,
            lengthCm = product.packageInfo.depthCm,
            widthCm = product.packageInfo.widthCm,
            heightCm = product.packageInfo.heightCm
        ).onSuccess { estimate ->
            _uiState.value = ProductDetailsUiState.Data(
                product = product,
                liked = liked,
                intercityEstimate = ProductIntercityEstimateUiState.Success(
                    estimate = estimate,
                    fromCity = fromCity,
                    toCity = toCity
                )
            )
        }.onFailure { error ->
            _uiState.value = ProductDetailsUiState.Data(
                product = product,
                liked = liked,
                intercityEstimate = ProductIntercityEstimateUiState.Error(
                    error.message ?: "Не удалось рассчитать доставку"
                )
            )
        }
    }

    private fun initialIntercityState(product: ProductDetailsUi): ProductIntercityEstimateUiState {
        if (!product.delivery.intercityEnabled) return ProductIntercityEstimateUiState.Disabled
        val fromCity = product.delivery.sellerPickupCity
        val toCity = product.delivery.buyerSavedCity
        return when {
            fromCity.isNullOrBlank() -> ProductIntercityEstimateUiState.MissingSellerAddress
            toCity.isNullOrBlank() -> ProductIntercityEstimateUiState.MissingBuyerAddress
            fromCity.equals(toCity, ignoreCase = true) -> ProductIntercityEstimateUiState.SameCity
            else -> ProductIntercityEstimateUiState.Loading
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
