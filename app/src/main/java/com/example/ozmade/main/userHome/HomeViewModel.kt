package com.example.ozmade.main.userHome

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    private fun loadTestData() {
        val myAds = listOf(
            AdBanner(id = "1", title = "Супер скидки!", imageRes = R.drawable.banner1),
        )

        val categoriesList = listOf<Category>()
        val productsList = listOf<Product>()

        _uiState.value = HomeUiState.Data(
            ads = myAds,
            categories = categoriesList,
            products = productsList
        )
    }

    fun load() {
        val myAds = listOf(
            AdBanner(id = "1", title = "Супер скидки!", imageRes = R.drawable.banner1),
        )

        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getHome() }
                .onSuccess { resp ->
                    _uiState.value = HomeUiState.Data(
                        ads = if (resp.ads.isNotEmpty()) resp.ads else myAds,
                        categories = resp.categories,
                        products = resp.products
                    )
                }
                .onFailure { e ->
                    Log.e("HomeViewModel", "Home load failed", e)
                    _uiState.value = HomeUiState.Error(e.message ?: "Ошибка загрузки Home")
                }
        }
        Log.d("HomeViewModel", "Repo impl = ${repo::class.java.name}")
    }

    fun toggleLike(productId: Int) {
        val currentState = _uiState.value
        if (currentState !is HomeUiState.Data) return

        viewModelScope.launch {
            runCatching {
                repo.toggleFavorite(productId)
            }.onSuccess { newLiked ->
                val updatedProducts = currentState.products.map { product ->
                    if (product.id == productId) {
                        product.copy(liked = newLiked)
                    } else {
                        product
                    }
                }

                _uiState.value = currentState.copy(products = updatedProducts)
            }.onFailure { e ->
                Log.e("HomeViewModel", "toggleLike failed", e)
            }
        }
    }
}