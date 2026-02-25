package com.example.ozmade.main.seller.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.orders.ui.OrdersList
import com.example.ozmade.main.seller.orders.data.SellerOrdersUiState
import com.example.ozmade.main.seller.orders.data.SellerOrdersViewModel

@Composable
fun SellerOrdersRoute(
    onOpenOrder: (Int) -> Unit,
    viewModel: SellerOrdersViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    when (ui) {
        is SellerOrdersUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        is SellerOrdersUiState.Error -> {
            val msg = (ui as SellerOrdersUiState.Error).message
            Column(Modifier.padding(16.dp)) {
                Text(msg, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.load() }, modifier = Modifier.fillMaxWidth()) { Text("Повторить") }
            }
        }

        is SellerOrdersUiState.Data -> {
            val orders = (ui as SellerOrdersUiState.Data).orders
            OrdersList(orders = orders, onOpen = onOpenOrder)
        }
    }
}