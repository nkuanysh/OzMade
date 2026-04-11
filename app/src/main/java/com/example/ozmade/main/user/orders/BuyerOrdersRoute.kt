package com.example.ozmade.main.user.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ozmade.main.orders.ui.OrdersList
import com.example.ozmade.main.user.orders.data.BuyerOrdersUiState
import com.example.ozmade.main.user.orders.data.BuyerOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerOrdersRoute(
    onBack: () -> Unit,
    onOpenOrder: (Int) -> Unit,
    viewModel: BuyerOrdersViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) { viewModel.load() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.load()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("История заказов") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        when (ui) {
            is BuyerOrdersUiState.Loading -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is BuyerOrdersUiState.Error -> {
                val msg = (ui as BuyerOrdersUiState.Error).message
                Column(Modifier.padding(padding).padding(16.dp)) {
                    Text(msg, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.load() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Повторить")
                    }
                }
            }

            is BuyerOrdersUiState.Data -> {
                val orders = (ui as BuyerOrdersUiState.Data).orders
                if (orders.isEmpty()) {
                    Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("У вас пока нет заказов", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    OrdersList(
                        orders = orders,
                        onOpen = onOpenOrder,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}