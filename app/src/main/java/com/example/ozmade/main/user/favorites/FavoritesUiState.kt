package com.example.ozmade.main.user.favorites

sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()
    data class Data(val items: List<FavoriteProductUi>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}