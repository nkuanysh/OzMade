package com.example.ozmade.main.seller.reviews

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SellerReviewsRoute(
    sellerId: String,
    onBack: () -> Unit,
    viewModel: SellerReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sellerId) {
        viewModel.load(sellerId)
    }

    SellerReviewsScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.load(sellerId) }
    )
}
