package com.example.ozmade.main.userHome.category

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CategoryRoute(
    categoryId: String,
    onBack: () -> Unit,
    onOpenProduct: (String) -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.load(categoryId)
    }

    CategoryScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.load(categoryId) },
        onOpenProduct = onOpenProduct
    )
}
