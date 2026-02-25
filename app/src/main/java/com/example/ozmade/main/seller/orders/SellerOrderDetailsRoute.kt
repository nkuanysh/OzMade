package com.example.ozmade.main.seller.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.orders.data.DeliveryType
import com.example.ozmade.main.orders.data.OrderStatus
import com.example.ozmade.main.orders.data.deliveryTitle
import com.example.ozmade.main.orders.data.statusTitle
import com.example.ozmade.main.seller.orders.data.SellerOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrderDetailsRoute(
    orderId: Int,
    onBack: () -> Unit,
    viewModel: SellerOrdersViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val order = remember(ui, orderId) { viewModel.findById(orderId) }

    var code by remember { mutableStateOf("") }
    var shipComment by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { if (order == null) viewModel.load() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Заказ #$orderId") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        if (order == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(Modifier.padding(padding).padding(16.dp)) {
            Text(order.productTitle, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Статус: ${statusTitle(order.status)}")
            Text("Доставка: ${deliveryTitle(order.deliveryType)}")
            Spacer(Modifier.height(8.dp))
            Text("Кол-во: ${order.quantity}")
            Text("Сумма: ${order.totalCost} ₸")
            Spacer(Modifier.height(12.dp))

            // действия продавца
            when (order.status) {
                OrderStatus.PENDING_SELLER -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { viewModel.cancel(order.id) }, modifier = Modifier.weight(1f)) {
                            Text("Отменить")
                        }
                        Button(onClick = { viewModel.confirm(order.id) }, modifier = Modifier.weight(1f)) {
                            Text("Подтвердить")
                        }
                    }
                }

                OrderStatus.CONFIRMED -> {
                    if (order.deliveryType == DeliveryType.INTERCITY) {
                        OutlinedTextField(
                            value = shipComment,
                            onValueChange = { shipComment = it },
                            label = { Text("Комментарий/трек (необязательно)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.shipped(order.id, shipComment.ifBlank { null }) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Отправил") }
                    } else {
                        Text("Когда товар передан покупателю — введи 4-значный код.")
                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it.filter { ch -> ch.isDigit() }.take(4) },
                            label = { Text("Код (4 цифры)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.complete(order.id, code) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = code.length == 4
                        ) { Text("Подтвердить выдачу") }
                    }
                }

                OrderStatus.READY_OR_SHIPPED -> {
                    Text(
                        "Ожидаем подтверждение получения покупателем (межгород) или завершение.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}