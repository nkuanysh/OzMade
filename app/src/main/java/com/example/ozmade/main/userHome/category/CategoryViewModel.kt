package com.example.ozmade.main.userHome.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.userHome.HomeRepository
import com.example.ozmade.main.userHome.details.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repo: HomeRepository,
    private val productRepo: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load(categoryId: String) {
        _uiState.value = CategoryUiState.Loading
        viewModelScope.launch {
            try {
                // 1. Get products filtered by category from backend
                val products = repo.getProductsByCategory(categoryId)
                
                // 2. Get the category details (to show title/icon)
                val homeData = repo.getHome()
                var category = homeData.categories.firstOrNull { it.id == categoryId }
                    ?: com.example.ozmade.main.userHome.Category(id = categoryId, title = categoryId.replaceFirstChar { it.uppercase() })

                // If no iconUrl from repo, provide a thematic background image
                if (category.iconUrl.isNullOrEmpty()) {
                    category = category.copy(iconUrl = imageForCategory(categoryId))
                }

                val quote = quoteForCategory(categoryId)

                _uiState.value = CategoryUiState.Data(
                    category = category,
                    headerQuote = quote,
                    products = products
                )
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error(e.message ?: "Ошибка загрузки категории")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val currentState = _uiState.value
        if (currentState is CategoryUiState.Data) {
            val filtered = if (query.isBlank()) {
                currentState.products
            } else {
                currentState.products.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.city.contains(query, ignoreCase = true)
                }
            }
            _uiState.value = currentState.copy(
                searchQuery = query,
                filteredProducts = filtered
            )
        }
    }

    private fun imageForCategory(id: String): String = when (id) {
        "food" -> "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&q=80"
        "clothes" -> "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=800&q=80"
        "art" -> "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=800&q=80"
        "craft" -> "https://images.unsplash.com/photo-1528150177508-7cc0c36cda5c?w=800&q=80"
        "gifts" -> "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?w=800&q=80"
        "holiday", "holidays" -> "https://images.unsplash.com/photo-1511272444580-d667c485207c?w=800&q=80"
        "home" -> "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=800&q=80"
        else -> "https://images.unsplash.com/photo-1516054101730-80436893699b?w=800&q=80"
    }

    private fun quoteForCategory(id: String): String = when (id) {
        "food" -> "Кулинария — это искусство, а искусство — это взрыв!"
        "clothes" -> "Стиль — это способ сказать кто ты, не говоря ни слова."
        "art" -> "Искусство начинается там, где заканчиваются слова."
        "craft" -> "Ручная работа — это душа, которую можно потрогать."
        "gifts" -> "Лучший подарок — сделанный с теплом."
        "holiday", "holidays" -> "Праздник — это эмоции, упакованные в детали."
        "home" -> "Дом начинается с вещей, которые хочется любить."
        else -> "Найди то, что сделано с душой."
    }

    fun toggleLike(productId: Int) {
        viewModelScope.launch {
            try {
                val isLikedNow = productRepo.toggleLike(productId)
                val currentState = _uiState.value
                if (currentState is CategoryUiState.Data) {
                    val newProducts = currentState.products.map {
                        if (it.id == productId) it.copy(liked = isLikedNow) else it
                    }
                    val newFiltered = currentState.filteredProducts.map {
                        if (it.id == productId) it.copy(liked = isLikedNow) else it
                    }
                    _uiState.value = currentState.copy(
                        products = newProducts,
                        filteredProducts = newFiltered
                    )
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }
}
