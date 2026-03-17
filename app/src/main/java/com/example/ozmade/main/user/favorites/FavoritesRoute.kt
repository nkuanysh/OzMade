package com.example.ozmade.main.user.favorites

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun FavoritesRoute(
    onBuyClick: () -> Unit = {},
    onOpenProduct: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadFavorites()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    FavoritesScreen(
        uiState = uiState,
        onBuyClick = onBuyClick,
        onOpenProduct = onOpenProduct,
        onRemoveFavorite = { productId ->
            viewModel.removeFromFavorites(productId)
        }
    )
}