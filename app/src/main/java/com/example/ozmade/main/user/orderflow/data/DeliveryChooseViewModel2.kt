package com.example.ozmade.main.user.orderflow.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.delivery.DeliveryEstimateRepository
import com.example.ozmade.main.delivery.IntercityDeliveryEstimate
import com.example.ozmade.main.delivery.extractCity
import com.example.ozmade.main.user.profile.data.ProfileRepository
import com.example.ozmade.main.userHome.details.ProductDetailsUi
import com.example.ozmade.main.userHome.details.ProductRepository
import com.example.ozmade.network.model.CreateOrderRequest
import com.example.ozmade.network.model.IntercityDeliveryOrderRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryChooseViewModel2 @Inject constructor(
    private val productRepo: ProductRepository,
    private val orderRepo: OrderFlowRepository,
    private val deliveryEstimateRepository: DeliveryEstimateRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    sealed class UiState {
        data object Loading : UiState()
        data class Error(val message: String) : UiState()
        data class Data(
            val product: ProductDetailsUi,
            val buyerName: String = "",
            val buyerPhone: String = "",
            val saving: Boolean = false,
            val actionError: String? = null,
            val intercityEstimate: IntercityEstimateState = IntercityEstimateState.Idle
        ) : UiState()
    }

    sealed class IntercityEstimateState {
        data object Idle : IntercityEstimateState()
        data object Loading : IntercityEstimateState()
        data object MissingBuyerAddress : IntercityEstimateState()
        data object MissingSellerAddress : IntercityEstimateState()
        data object SameCity : IntercityEstimateState()
        data class Success(
            val estimate: IntercityDeliveryEstimate,
            val fromCity: String,
            val toCity: String
        ) : IntercityEstimateState()
        data class Error(val message: String) : IntercityEstimateState()
    }

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    fun load(productId: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching {
                val product = productRepo.getProductDetails(productId)
                val profile = runCatching { profileRepo.getMyProfile() }.getOrNull()
                Triple(product, profile?.name.orEmpty(), profile?.phone.orEmpty())
            }
                .onSuccess { (product, buyerName, buyerPhone) ->
                    _state.value = UiState.Data(
                        product = product,
                        buyerName = buyerName,
                        buyerPhone = buyerPhone
                    )
                }
                .onFailure { _state.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun estimateIntercityDelivery(toAddressText: String?) {
        val current = _state.value as? UiState.Data ?: return
        val product = current.product
        val fromAddress = product.delivery.pickupAddress?.takeIf { it.isNotBlank() }
            ?: product.seller.address.takeIf { it.isNotBlank() }
        val fromCity = product.delivery.sellerPickupCity ?: extractCity(fromAddress)
        val toCity = extractCity(toAddressText) ?: product.delivery.buyerSavedCity

        val nextState = when {
            fromCity.isNullOrBlank() -> IntercityEstimateState.MissingSellerAddress
            toCity.isNullOrBlank() || toAddressText.isNullOrBlank() -> IntercityEstimateState.MissingBuyerAddress
            fromCity.equals(toCity, ignoreCase = true) -> IntercityEstimateState.SameCity
            else -> null
        }

        if (nextState != null) {
            _state.value = current.copy(intercityEstimate = nextState)
            return
        }

        viewModelScope.launch {
            _state.value = current.copy(intercityEstimate = IntercityEstimateState.Loading)
            deliveryEstimateRepository.estimateIntercityDelivery(
                fromCity = fromCity!!,
                toCity = toCity!!,
                weightGrams = product.packageInfo.weightGrams,
                lengthCm = product.packageInfo.depthCm,
                widthCm = product.packageInfo.widthCm,
                heightCm = product.packageInfo.heightCm
            ).onSuccess { estimate ->
                val cur = _state.value
                if (cur is UiState.Data) {
                    _state.value = cur.copy(
                        intercityEstimate = IntercityEstimateState.Success(
                            estimate = estimate,
                            fromCity = fromCity,
                            toCity = toCity
                        )
                    )
                }
            }.onFailure { error ->
                val cur = _state.value
                if (cur is UiState.Data) {
                    _state.value = cur.copy(
                        intercityEstimate = IntercityEstimateState.Error(
                            error.message ?: "Не удалось рассчитать доставку"
                        )
                    )
                }
            }
        }
    }

    fun createOrder(
        productId: Int,
        quantity: Int,
        deliveryType: String,
        shippingAddressText: String?,
        shippingLat: Double? = null,
        shippingLng: Double? = null,
        shippingComment: String? = null,
        intercityDelivery: IntercityDeliveryOrderRequest? = null,
        onSuccess: () -> Unit
    ) {
        val current = _state.value
        if (current !is UiState.Data) return

        viewModelScope.launch {
            _state.value = current.copy(saving = true, actionError = null)

            runCatching {
                orderRepo.create(
                    CreateOrderRequest(
                        productId = productId,
                        quantity = quantity,
                        deliveryType = deliveryType,
                        shippingAddressText = shippingAddressText,
                        shippingLat = shippingLat,
                        shippingLng = shippingLng,
                        shippingComment = shippingComment,
                        intercityDelivery = intercityDelivery
                    )
                )
            }.onSuccess {
                onSuccess()
            }.onFailure {
                val cur = _state.value
                if (cur is UiState.Data) {
                    // Try to parse error message if it's from backend validation
                    val msg = it.message ?: "Ошибка"
                    _state.value = cur.copy(saving = false, actionError = msg)
                }
            }
        }
    }
}
