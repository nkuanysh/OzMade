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
                
                // Only completed orders contribute to revenue
                val completedOrders = orders.filter { it.status == OrderStatus.COMPLETED }
                
                val totalRevenue = completedOrders.sumOf { it.totalCost }
                // Count only active or completed orders for analytics (excluding cancelled/expired)
                val relevantOrdersCount = orders.count { 
                    it.status != OrderStatus.CANCELLED_BY_BUYER && 
                    it.status != OrderStatus.CANCELLED_BY_SELLER && 
                    it.status != OrderStatus.EXPIRED 
                }

                // Calculate popular products based on completed orders
                val popularProducts = completedOrders.groupBy { it.productId }
                    .map { (id, productOrders) ->
                        PopularProductUi(
                            name = productOrders.first().productTitle,
                            salesCount = productOrders.sumOf { it.quantity },
                            revenue = productOrders.sumOf { it.totalCost },
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
                    revenueGrowth = 0.0, // Should be calculated comparing with previous period
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