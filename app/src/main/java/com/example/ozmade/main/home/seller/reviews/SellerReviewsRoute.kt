package com.example.ozmade.main.home.seller.reviews

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SellerReviewsRoute(
    sellerId: String,
    onBack: () -> Unit,
    onOpenProduct: (String) -> Unit,
    viewModel: SellerReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sellerId) {
        viewModel.load(sellerId)
    }

    SellerReviewsScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.load(sellerId) },
        onOpenProduct = onOpenProduct
    )
}
