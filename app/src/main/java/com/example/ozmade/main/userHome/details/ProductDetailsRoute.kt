package com.example.ozmade.main.userHome.details

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProductDetailsRoute(
    productId: String,
    onBack: () -> Unit,
    onChat: (sellerId: String, sellerName: String, productId: String, productTitle: String, price: Double) -> Unit,
    onOrder: (qty: Int) -> Unit,
    onOpenReviews: (String) -> Unit,
    onOpenSeller: (String) -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
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
            val p = state.product

            ProductDetailsScreen(
                product = p,
                liked = state.liked,
                onToggleLike = { viewModel.toggleLike() },
                onShare = { /* TODO: share */ },
                onChat = {
                    onChat(
                        p.seller.id,
                        p.seller.name,
                        p.id,
                        p.title,
                        p.price.toDouble()
                    )
                },
                onOrder = { qty ->
                    onOrder(qty)
                },
                onOpenReviews = onOpenReviews,
                onOpenSeller = onOpenSeller,
                onBack = onBack
            )
        }
    }
}