package com.example.ozmade.main.userHome

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenProduct: (Int) -> Unit,
    onOpenCategory: (String) -> Unit,
    onBecomeSeller: () -> Unit,
    onSupportClick: () -> Unit,
    onSearchClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Refresh favorites when returning to this screen
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshFavorites()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HomeScreen(
        uiState = uiState,
        onOpenProduct = onOpenProduct,
        onOpenCategory = onOpenCategory,
        onFavoriteClick = { productId ->
            viewModel.toggleLike(productId.toInt())
        },
        onSearchClick = onSearchClick,
        onSearchQueryChange = { query ->
            viewModel.onSearchQueryChange(query)
        },
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
        },
        onAdClick = { ad ->
            when (ad.id) {
                "1" -> onBecomeSeller()
                "2" -> onOpenCategory("craft")
                "3" -> onSupportClick()
            }
        },
        onRetry = { viewModel.load() }
    )
}
