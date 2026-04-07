package com.example.ozmade.main.userHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.network.api.OzMadeApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: OzMadeApi
) : ViewModel() {

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
                val adsResp = api.getAds()
                // val catsResp = api.getCategories() // If needed

                if (productsResp.isSuccessful) {
                    val products = productsResp.body()?.map { dto ->
                        Product(
                            id = dto.id,
                            title = dto.title ?: "Без названия",
                            price = dto.price ?: 0.0,
                            imageUrl = dto.imageUrl ?: ""
                        )
                    } ?: emptyList()

                    val ads = adsResp.body()?.map { dto ->
                        AdBanner(id = dto.id, title = dto.title, imageUrl = dto.imageUrl)
                    } ?: emptyList()

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
                        ads = ads
                    )
                } else {
                    _uiState.value = HomeUiState.Error("Ошибка сервера: ${productsResp.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Data) {
            _uiState.value = currentState.copy(searchQuery = query)
            // Здесь можно добавить логику фильтрации или запроса к API
        }
    }

    fun toggleLike(productId: Int) {
        viewModelScope.launch {
            try {
                api.toggleFavorite(productId)
                // Обновляем локальное состояние
                val currentState = _uiState.value
                if (currentState is HomeUiState.Data) {
                    val newProducts = currentState.products.map {
                        if (it.id == productId) it.copy(liked = !it.liked) else it
                    }
                    _uiState.value = currentState.copy(products = newProducts)
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }
}
