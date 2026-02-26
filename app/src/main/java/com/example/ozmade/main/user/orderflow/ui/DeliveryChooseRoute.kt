package com.example.ozmade.main.user.orderflow.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.orders.data.DeliveryType
import com.example.ozmade.main.userHome.details.DeliveryInfoUi
import com.example.ozmade.main.user.orderflow.data.DeliveryChooseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryChooseRoute(
    productId: Int,
    title: String,
    price: Double,
    quantity: Int,
    delivery: DeliveryInfoUi,
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: DeliveryChooseViewModel = hiltViewModel()
) {
    var selected by remember { mutableStateOf<String?>(null) }
    var shippingAddress by remember { mutableStateOf("") }

    val saving by viewModel.saving.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Способ доставки") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {

            Text(title, style = MaterialTheme.typography.titleLarge)
            Text("Цена: $price ₸   Кол-во: $quantity")
            Text("Итого: ${price * quantity} ₸")

            Spacer(Modifier.height(14.dp))

            if (delivery.pickupEnabled) {
                DeliveryOption(
                    title = "Самовывоз",
                    subtitle = delivery.pickupTime ?: "есть",
                    selected = selected == DeliveryType.PICKUP,
                    onClick = { selected = DeliveryType.PICKUP }
                )
                Spacer(Modifier.height(10.dp))
            }

            if (delivery.freeDeliveryEnabled) {
                DeliveryOption(
                    title = "Моя доставка",
                    subtitle = "Посмотреть зону доставки",
                    selected = selected == DeliveryType.MY_DELIVERY,
                    onClick = { selected = DeliveryType.MY_DELIVERY }
                )
                Spacer(Modifier.height(10.dp))
            }

            if (delivery.intercityEnabled) {
                DeliveryOption(
                    title = "Межгород",
                    subtitle = "Заполните адрес доставки",
                    selected = selected == DeliveryType.INTERCITY,
                    onClick = { selected = DeliveryType.INTERCITY }
                )
                if (selected == DeliveryType.INTERCITY) {
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = shippingAddress,
                        onValueChange = { shippingAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Город, район, улица, дом, кв") }
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            if (!error.isNullOrBlank()) {
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.createOrder(
                        productId = productId,
                        quantity = quantity,
                        deliveryType = selected,
                        shippingAddressText = if (selected == DeliveryType.INTERCITY) shippingAddress else null,
                        onSuccess = onCreated
                    )
                },
                enabled = !saving && selected != null && (selected != DeliveryType.INTERCITY || shippingAddress.isNotBlank()),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (saving) CircularProgressIndicator(modifier = Modifier.size(18.dp))
                else Text("Заказать сейчас")
            }
        }
    }
}

@Composable
private fun DeliveryOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(onClick = onClick) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                RadioButton(selected = selected, onClick = onClick)
            }
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}