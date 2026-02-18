package com.example.ozmade.main.seller

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SellerRoute(
    sellerId: String,
    onBack: () -> Unit,
    onOpenProduct: (String) -> Unit,
    onOpenSellerReviews: (String) -> Unit,
    viewModel: SellerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sellerId) {
        viewModel.load(sellerId)
    }

    SellerScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.load(sellerId) },
        onSearchChanged = { /* screen keeps local state */ },
        onToggleLike = { pid -> viewModel.toggleLike(pid) },
        onOpenProduct = onOpenProduct,
        onOpenSellerReviews = { onOpenSellerReviews(sellerId) }

    )
}
