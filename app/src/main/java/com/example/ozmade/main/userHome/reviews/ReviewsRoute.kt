package com.example.ozmade.main.userHome.reviews

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReviewsRoute(
    productId: String,
    onBack: () -> Unit,
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.load(productId)
    }

    ReviewsScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.load(productId) }
    )
}
