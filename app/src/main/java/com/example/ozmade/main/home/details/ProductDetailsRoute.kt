package com.example.ozmade.main.home.details

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProductDetailsRoute(
    productId: String,
    onBack: () -> Unit,
    onChat: () -> Unit,
    onOrder: () -> Unit,
    onOpenReviews: (String) -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.load(productId)
    }

    when (val state = uiState) {
        is ProductDetailsUiState.Loading -> {
            ProductDetailsLoading(onBack = onBack)
        }

        is ProductDetailsUiState.Error -> {
            ProductDetailsError(
                message = state.message,
                onBack = onBack,
                onRetry = { viewModel.load(productId) }
            )
        }

        is ProductDetailsUiState.Data -> {
            ProductDetailsScreen(
                product = state.product,
                liked = state.liked,
                onToggleLike = { viewModel.toggleLike() },
                onShare = { /* TODO: share */ },
                onChat = onChat,
                onOrder = onOrder,
                onOpenReviews = onOpenReviews
            )
        }
    }
}
