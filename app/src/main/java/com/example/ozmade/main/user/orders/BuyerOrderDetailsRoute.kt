package com.example.ozmade.main.user.orders

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
import com.example.ozmade.main.user.orders.data.BuyerOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerOrderDetailsRoute(
    orderId: Int,
    onBack: () -> Unit,
    onChat: (Int) -> Unit = {}, // если захочешь открыть чат по заказу
    onOpenProduct: (Int) -> Unit = {},
    viewModel: BuyerOrdersViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val order = remember(ui, orderId) { viewModel.findById(orderId) }

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
            Spacer(Modifier.height(10.dp))

            // Детали доставки
            when (order.deliveryType) {
                DeliveryType.PICKUP -> {
                    Text("Адрес: ${order.pickupAddress ?: "—"}")
                    Text("Время: ${order.pickupTime ?: "—"}")
                }
                DeliveryType.MY_DELIVERY -> {
                    Text("Зона: ${order.zoneCenterAddress ?: "—"}")
                    Text("Радиус: ${order.zoneRadiusKm?.toString() ?: "—"} км")
                }
                DeliveryType.INTERCITY -> {
                    Text("Адрес доставки:")
                    Text(order.shippingAddressText ?: "—")
                }
            }

            Spacer(Modifier.height(14.dp))

            // Кнопки покупателя по статусу
            when (order.status) {
                OrderStatus.PENDING_SELLER -> {
                    Button(
                        onClick = { viewModel.cancel(order.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Отменить заказ") }
                }

                OrderStatus.CONFIRMED -> {
                    // Код можно показывать (если бэк отдаёт)
                    if (!order.confirmCode.isNullOrBlank()) {
                        Card {
                            Column(Modifier.padding(12.dp)) {
                                Text("Код подтверждения (скажи продавцу):", style = MaterialTheme.typography.bodyMedium)
                                Text(order.confirmCode, style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    OutlinedButton(
                        onClick = { onChat(order.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Открыть чат") }
                }

                OrderStatus.READY_OR_SHIPPED -> {
                    if (order.deliveryType == DeliveryType.INTERCITY) {
                        Button(
                            onClick = { viewModel.received(order.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Получил") }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Нажимай только после получения. После этого можно оставить отзыв.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            "Ожидается выдача/доставка. После передачи продавец введёт код.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OrderStatus.COMPLETED -> {
                    Button(onClick = { /* TODO: открыть экран отзыва по orderId */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Оставить отзыв")
                    }
                }
            }
        }
    }
}