package com.example.ozmade.main.userHome

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.R
import com.example.ozmade.main.userHome.details.ProductRepository
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.utils.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: OzMadeApi,
    private val repo: ProductRepository
) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                coroutineScope {
                    val productsRespDeferred = async { api.getProducts() }
                    val favoritesDeferred = async { runCatching { repo.getFavorites() }.getOrDefault(emptyList()) }

                    val productsResp = productsRespDeferred.await()
                    val favorites = favoritesDeferred.await()
                    val favoriteIds = favorites.map { it.id }.toSet()

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
                                imageUrl = formattedUrl,
                                liked = favoriteIds.contains(dto.id),
                                rating = dto.averageRating ?: 4.5
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
                }
            } catch (e: Exception) {
                Log.e(TAG, "load: Exception", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun refreshFavorites() {
        viewModelScope.launch {
            try {
                val favorites = repo.getFavorites()
                val favoriteIds = favorites.map { it.id }.toSet()
                val currentState = _uiState.value
                if (currentState is HomeUiState.Data) {
                    val newProducts = currentState.products.map {
                        it.copy(liked = favoriteIds.contains(it.id))
                    }
                    _uiState.value = currentState.copy(products = newProducts)
                }
            } catch (e: Exception) {
                Log.e(TAG, "refreshFavorites error", e)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Data) {
            _uiState.value = currentState.copy(searchQuery = query)
            
            searchJob?.cancel()
            if (query.isBlank()) {
                load()
            } else {
                searchJob = viewModelScope.launch {
                    delay(500) // Debounce
                    try {
                        coroutineScope {
                            val responseDeferred = async { api.searchProducts(query = query) }
                            val favoritesDeferred = async { runCatching { repo.getFavorites() }.getOrDefault(emptyList()) }

                            val response = responseDeferred.await()
                            val favoriteIds = favoritesDeferred.await().map { it.id }.toSet()

                            if (response.isSuccessful) {
                                val searchedProducts = response.body()?.map { dto ->
                                    Product(
                                        id = dto.id,
                                        title = dto.title ?: "Без названия",
                                        price = dto.price ?: 0.0,
                                        imageUrl = ImageUtils.formatImageUrl(dto.images?.firstOrNull() ?: dto.imageUrl),
                                        liked = favoriteIds.contains(dto.id),
                                        rating = dto.averageRating ?: 4.5
                                    )
                                } ?: emptyList()

                                val updatedState = _uiState.value
                                if (updatedState is HomeUiState.Data) {
                                    _uiState.value = updatedState.copy(products = searchedProducts)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "search error", e)
                    }
                }
            }
        }
    }

    fun toggleLike(productId: Int) {
        viewModelScope.launch {
            try {
                val isLikedNow = repo.toggleLike(productId)
                val currentState = _uiState.value
                if (currentState is HomeUiState.Data) {
                    val newProducts = currentState.products.map {
                        if (it.id == productId) it.copy(liked = isLikedNow) else it
                    }
                    _uiState.value = currentState.copy(products = newProducts)
                }
            } catch (e: Exception) {
                Log.e(TAG, "toggleLike: Error", e)
            }
        }
    }
}
