package com.example.ozmade.main.userHome

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.R
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.utils.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: OzMadeApi
) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val productsResp = api.getProducts()
                Log.d(TAG, "load: products successful=${productsResp.isSuccessful}")

                if (productsResp.isSuccessful) {
                    val products = productsResp.body()?.map { dto ->
                        val rawImg = dto.images?.firstOrNull() ?: dto.imageUrl
                        val formattedUrl = ImageUtils.formatImageUrl(rawImg)
                        
                        Log.d(TAG, "Product ID=${dto.id}: rawImg=$rawImg -> formatted=$formattedUrl")

                        Product(
                            id = dto.id,
                            title = dto.title ?: "Без названия",
                            price = dto.price ?: 0.0,
                            imageUrl = formattedUrl
                        )
                    } ?: emptyList()

                    // Local promotional banners
                    val localAds = listOf(
                        AdBanner(id = "1", title = "Присоединяйся к клубу творцов!", imageRes = R.drawable.banner1),
                        AdBanner(id = "2", title = "Лучшие товары ручной работы", imageRes = R.drawable.banner2),
                        AdBanner(id = "3", title = "Скидки для мастеров", imageRes = R.drawable.banner3)
                    )

                    _uiState.value = HomeUiState.Data(
                        products = products,
                        categories = listOf(
                            Category("food", "Еда"),
                            Category("clothes", "Одежда"),
                            Category("art", "Искусство"),
                            Category("craft", "Ремесло"),
                            Category("gifts", "Подарки"),
                            Category("home", "Для дома")
                        ),
                        ads = localAds
                    )
                } else {
                    Log.e(TAG, "load: Error code ${productsResp.code()}")
                    _uiState.value = HomeUiState.Error("Ошибка сервера: ${productsResp.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "load: Exception", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Data) {
            _uiState.value = currentState.copy(searchQuery = query)
        }
    }

    fun toggleLike(productId: Int) {
        viewModelScope.launch {
            try {
                api.toggleFavorite(productId)
                val currentState = _uiState.value
                if (currentState is HomeUiState.Data) {
                    val newProducts = currentState.products.map {
                        if (it.id == productId) it.copy(liked = !it.liked) else it
                    }
                    _uiState.value = currentState.copy(products = newProducts)
                }
            } catch (e: Exception) {
                Log.e(TAG, "toggleLike: Error", e)
            }
        }
    }
}
