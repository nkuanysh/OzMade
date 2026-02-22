package com.example.ozmade.main.seller.products

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SellerProductsRoute(
    onAddProduct: () -> Unit,
    onOpenEdit: (String) -> Unit
) {
    val vm: SellerProductsViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    SellerProductsScreen(
        state = state,
        onRetry = vm::load,
        onQueryChange = vm::onQueryChange,
        onFilterChange = vm::onFilterChange,
        onAddProduct = onAddProduct,
        onOpenEdit = onOpenEdit,
        onUpdatePrice = vm::updatePrice,
        onToggleSale = vm::toggleSale,
        onDelete = vm::delete,
        onDismissError = { /* можно сделать отдельным событием, но пока ок */ }
    )
}