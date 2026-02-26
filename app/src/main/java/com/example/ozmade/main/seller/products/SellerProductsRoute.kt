package com.example.ozmade.main.seller.products

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun SellerProductsRoute(
    onAddProduct: () -> Unit,
    onOpenEdit: (Int) -> Unit
) {
    val vm: SellerProductsViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { vm.load() }

    LaunchedEffect(state.error) {
        state.error?.let { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
            vm.clearError()
        }
    }

    LaunchedEffect(state.message) {
        state.message?.let { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
            vm.consumeMessage()
        }
    }

    SellerProductsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onRetry = vm::load,
        onQueryChange = vm::onQueryChange,
        onFilterChange = vm::onFilterChange,
        onAddProduct = onAddProduct,
        onOpenEdit = onOpenEdit,
        onUpdatePrice = vm::updatePrice,
        onToggleSale = vm::toggleSale,
        onDelete = { id -> vm.delete(id) },
        onDismissError = vm::clearError
    )
}