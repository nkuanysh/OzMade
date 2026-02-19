package com.example.ozmade.main.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.home.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repo: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load(categoryId: String) {
        _uiState.value = CategoryUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getHome() }
                .onSuccess { home ->
                    val category = home.categories.firstOrNull { it.id == categoryId }
                        ?: return@onSuccess run {
                            _uiState.value = CategoryUiState.Error("Категория не найдена")
                        }

                    val products = home.products.filter { it.categoryId == categoryId }

                    // Заглушка цитаты/слогана (потом можно сделать полем на бэкенде)
                    val quote = quoteForCategory(categoryId)

                    _uiState.value = CategoryUiState.Data(
                        category = category,
                        headerQuote = quote,
                        products = products
                    )
                }
                .onFailure {
                    _uiState.value = CategoryUiState.Error(it.message ?: "Ошибка загрузки категории")
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
}
