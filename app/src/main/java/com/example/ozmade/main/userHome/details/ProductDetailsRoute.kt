package com.example.ozmade.main.userHome.details

import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProductDetailsRoute(
    productId: Int,
    onBack: () -> Unit,
    onChat: (sellerId: Int, sellerName: String, productId: Int, productTitle: String, price: Double) -> Unit,
    onOpenDelivery: (productId: Int, qty: Int) -> Unit,
    onOpenReviews: (Int) -> Unit,
    onOpenSeller: (Int) -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
                onShare = {
                    val shareLink = "http://34.178.41.41:8080/products/${p.id}"
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Посмотрите этот товар на OzMade: $shareLink")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Поделиться товаром"))
                },
                onChat = {
                    onChat(
                        p.seller.id,
                        p.seller.name,
                        p.id,
                        p.title,
                        p.price.toDouble()
                    )
                },
                onOpenDelivery = { qty ->
                    onOpenDelivery(p.id, qty)
                },
                onOpenReviews = onOpenReviews,
                onOpenSeller = onOpenSeller,
                onBack = onBack
            )
        }
    }
}
