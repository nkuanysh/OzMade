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
                val category = homeData.categories.firstOrNull { it.id == categoryId }
                    ?: com.example.ozmade.main.userHome.Category(id = categoryId, title = categoryId.replaceFirstChar { it.uppercase() })

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

    private fun quoteForCategory(id: String): String = when (id) {
        "food" -> "Кулинария — это искусство, а искусство — это взрыв!"
        "clothes" -> "Стиль — это способ сказать кто ты, не говоря ни слова."
        "art" -> "Искусство начинается там, где заканчиваются слова."
        "craft" -> "Ручная работа — это душа, которую можно потрогать."
        "gifts" -> "Лучший подарок — сделанный с теплом."
        "holidays" -> "Праздник — это эмоции, упакованные в детали."
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
                    _uiState.value = currentState.copy(products = newProducts)
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }
}
