package com.example.ozmade.main.seller.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.orders.data.OrderStatus
import com.example.ozmade.main.seller.data.SellerRepository
import com.example.ozmade.main.seller.orders.data.SellerOrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val totalRevenue: Double = 0.0,
    val revenueGrowth: Double = 0.0,
    val ordersCount: Int = 0,
    val viewsCount: Int = 0,
    val popularProducts: List<PopularProductUi> = emptyList(),
    val error: String? = null
)

data class PopularProductUi(
    val name: String,
    val salesCount: Int,
    val revenue: Double,
    val imageUrl: String? = null
)

@HiltViewModel
class SellerAnalyticsViewModel @Inject constructor(
    private val sellerRepo: SellerRepository,
    private val ordersRepo: SellerOrdersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // 1. Get orders to calculate revenue and popular products
                val orders = ordersRepo.getOrders()
                
                // Logic Fix: Successful orders are those not cancelled or expired.
                // We count them all in "Orders" to be consistent with what the seller sees in "My Orders".
                val successfulOrders = orders.filter { 
                    it.status != OrderStatus.CANCELLED_BY_BUYER &&
                    it.status != OrderStatus.CANCELLED_BY_SELLER &&
                    it.status != OrderStatus.EXPIRED
                }

                // Logic Fix: Total Revenue should include ALL successful orders (In-progress + Completed)
                // to match the "Popular Products" revenue and show the total volume (GMV).
                // If we only show Completed, the card might show $0 while the products list shows thousands.
                val totalRevenue = successfulOrders.sumOf { 
                    if (it.totalCost > 0) it.totalCost else it.price * it.quantity 
                }
                
                val relevantOrdersCount = successfulOrders.size

                // Calculate popular products based on ALL successful orders
                val popularProducts = successfulOrders.groupBy { it.productId }
                    .map { (id, productOrders) ->
                        PopularProductUi(
                            name = productOrders.first().productTitle,
                            salesCount = productOrders.sumOf { it.quantity },
                            revenue = productOrders.sumOf { 
                                if (it.totalCost > 0) it.totalCost else it.price * it.quantity 
                            },
                            imageUrl = productOrders.first().productImageUrl
                        )
                    }
                    .sortedByDescending { it.salesCount }
                    .take(5)

                // 2. Get views from real product data
                val products = sellerRepo.getMyProducts()
                val viewsCount = products.sumOf { it.viewCount }

                _uiState.value = AnalyticsUiState(
                    isLoading = false,
                    totalRevenue = totalRevenue,
                    revenueGrowth = 0.0,
                    ordersCount = relevantOrdersCount,
                    viewsCount = viewsCount,
                    popularProducts = popularProducts
                )
            } catch (e: Exception) {
                _uiState.value = AnalyticsUiState(isLoading = false, error = e.message ?: "Ошибка загрузки данных")
            }
        }
    }
}