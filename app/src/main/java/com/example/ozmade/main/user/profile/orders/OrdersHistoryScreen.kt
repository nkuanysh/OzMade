package com.example.ozmade.main.user.profile.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.orders.ui.OrdersList
import com.example.ozmade.main.user.orders.data.BuyerOrdersUiState
import com.example.ozmade.main.user.orders.data.BuyerOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(
    onBack: () -> Unit,
    viewModel: BuyerOrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.ui.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "История заказов",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        when (val state = uiState) {
            is BuyerOrdersUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF9800))
                }
            }
            is BuyerOrdersUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.load() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text("Повторить")
                    }
                }
            }
            is BuyerOrdersUiState.Data -> {
                if (state.orders.isEmpty()) {
                    EmptyOrdersView(padding)
                } else {
                    OrdersList(
                        orders = state.orders,
                        onOpen = { /* Можно добавить открытие деталей заказа */ },
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyOrdersView(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.LightGray
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Заказов пока нет",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Ваши покупки будут отображаться здесь",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
