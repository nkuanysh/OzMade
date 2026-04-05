package com.example.ozmade.main.userHome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenProduct: (Int) -> Unit,
    onOpenCategory: (String) -> Unit,
    onSearchClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onOpenProduct = onOpenProduct,
        onOpenCategory = onOpenCategory,
        onFavoriteClick = { productId ->
            viewModel.toggleLike(productId.toInt())
        },
        onSearchClick = onSearchClick,
        onRetry = { viewModel.load() }
    )
}
