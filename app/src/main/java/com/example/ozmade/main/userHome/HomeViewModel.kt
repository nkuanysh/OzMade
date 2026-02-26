package com.example.ozmade.main.userHome

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.ozmade.R

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

        val categoriesList = listOf<Category>() // твои категории
        val productsList = listOf<Product>() // твои продукты

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
                        ads = myAds,
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
}
