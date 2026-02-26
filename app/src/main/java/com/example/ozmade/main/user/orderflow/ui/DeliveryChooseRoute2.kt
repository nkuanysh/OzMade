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
import com.example.ozmade.main.user.orderflow.data.DeliveryChooseViewModel2
import com.example.ozmade.main.userHome.details.ProductDetailsUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryChooseRoute2(
    productId: Int,
    quantity: Int,
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: DeliveryChooseViewModel2 = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(productId) { viewModel.load(productId) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Способ доставки") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        when (val st = state) {
            is DeliveryChooseViewModel2.UiState.Loading -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DeliveryChooseViewModel2.UiState.Error -> {
                Column(Modifier.padding(padding).padding(16.dp)) {
                    Text(st.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.load(productId) }, modifier = Modifier.fillMaxWidth()) { Text("Повторить") }
                }
            }
            is DeliveryChooseViewModel2.UiState.Data -> {
                DeliveryChooseContent(
                    product = st.product,
                    quantity = quantity,
                    saving = st.saving,
                    error = st.actionError,
                    onBack = onBack,
                    onCreate = { deliveryType, shippingAddress ->
                        viewModel.createOrder(
                            productId = productId,
                            quantity = quantity,
                            deliveryType = deliveryType,
                            shippingAddressText = shippingAddress,
                            onSuccess = onCreated
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DeliveryChooseContent(
    product: ProductDetailsUi,
    quantity: Int,
    saving: Boolean,
    error: String?,
    onBack: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var selected by remember { mutableStateOf<String?>(null) }
    var shippingAddress by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        Text(product.title, style = MaterialTheme.typography.titleLarge)
        Text("Цена: ${product.price} ₸   Кол-во: $quantity")
        Text("Итого: ${product.price * quantity} ₸")

        Spacer(Modifier.height(14.dp))

        val d = product.delivery

        if (d.pickupEnabled) {
            DeliveryOption(
                title = "Самовывоз",
                subtitle = "Адрес: ${product.seller.address}\nВремя: ${d.pickupTime ?: "—"}",
                selected = selected == DeliveryType.PICKUP,
                onClick = { selected = DeliveryType.PICKUP }
            )
            Spacer(Modifier.height(10.dp))
        }

        if (d.freeDeliveryEnabled) {
            DeliveryOption(
                title = "Моя доставка",
                subtitle = "Посмотреть зону доставки (пока заглушка/карта позже)",
                selected = selected == DeliveryType.MY_DELIVERY,
                onClick = { selected = DeliveryType.MY_DELIVERY }
            )
            Spacer(Modifier.height(10.dp))
        }

        if (d.intercityEnabled) {
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
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                val type = selected ?: return@Button
                val addr = if (type == DeliveryType.INTERCITY) shippingAddress else null
                onCreate(type, addr)
            },
            enabled = !saving && selected != null && (selected != DeliveryType.INTERCITY || shippingAddress.isNotBlank()),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (saving) CircularProgressIndicator(modifier = Modifier.size(18.dp))
            else Text("Заказать сейчас")
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