package com.example.ozmade.main.category

import com.example.ozmade.main.home.Category
import com.example.ozmade.main.home.Product

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()

    data class Data(
        val category: Category,
        val headerQuote: String,
        val products: List<Product>
    ) : CategoryUiState()
}
