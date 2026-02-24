package com.example.ozmade.main.userHome.category

import com.example.ozmade.main.userHome.Category
import com.example.ozmade.main.userHome.Product

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()

    data class Data(
        val category: Category,
        val headerQuote: String,
        val products: List<Product>
    ) : CategoryUiState()
}
