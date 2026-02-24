package com.example.ozmade.main.seller.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDeliveryRoute(
    onBack: () -> Unit,
    viewModel: SellerDeliveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var snackbar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Доставка") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(remember { SnackbarHostState() })
        }
    ) { padding ->

        when (uiState) {
            is SellerDeliveryUiState.Loading -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is SellerDeliveryUiState.Error -> {
                val msg = (uiState as SellerDeliveryUiState.Error).message
                Column(
                    Modifier.padding(padding).padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(msg, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.load() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Повторить")
                    }
                }
            }

            is SellerDeliveryUiState.Data -> {
                val s = (uiState as SellerDeliveryUiState.Data).ui
                val scroll = rememberScrollState()

                Column(
                    Modifier.padding(padding)
                        .verticalScroll(scroll)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // 1) Самовывоз
                    DeliveryCard(
                        title = "Самовывоз",
                        enabled = s.pickupEnabled,
                        onToggle = { on ->
                            viewModel.updateLocal { it.copy(pickupEnabled = on) }
                        }
                    ) {
                        if (s.pickupEnabled) {
                            OutlinedTextField(
                                value = s.pickupAddress,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(pickupAddress = v) } },
                                label = { Text("Адрес") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = s.pickupTime,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(pickupTime = v) } },
                                label = { Text("Время (например 10:00 - 18:00)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(Modifier.height(12.dp))
                            SaveCancelRow(
                                onSave = { viewModel.savePickup { snackbar = it } },
                                onCancel = { viewModel.revert() }
                            )
                        }
                    }

                    // 2) Моя доставка (радиус)
                    DeliveryCard(
                        title = "Моя доставка",
                        enabled = s.myDeliveryEnabled,
                        onToggle = { on ->
                            viewModel.updateLocal { it.copy(myDeliveryEnabled = on) }
                        }
                    ) {
                        if (s.myDeliveryEnabled) {
                            Text(
                                "Пока без карты: введи координаты точки (позже подключим карту).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = s.centerLat,
                                    onValueChange = { v -> viewModel.updateLocal { it.copy(centerLat = v) } },
                                    label = { Text("Lat") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = s.centerLng,
                                    onValueChange = { v -> viewModel.updateLocal { it.copy(centerLng = v) } },
                                    label = { Text("Lng") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = s.centerAddress,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(centerAddress = v) } },
                                label = { Text("Адрес точки (необязательно)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(12.dp))

                            Text("Радиус: ${s.radiusKm} км", style = MaterialTheme.typography.titleSmall)
                            Slider(
                                value = s.radiusKm.toFloat(),
                                onValueChange = { v ->
                                    viewModel.updateLocal { it.copy(radiusKm = v.toInt().coerceIn(1, 20)) }
                                },
                                valueRange = 1f..20f
                            )

                            SaveCancelRow(
                                onSave = { viewModel.saveMyDelivery { snackbar = it } },
                                onCancel = { viewModel.revert() }
                            )
                        }
                    }

                    // 3) Межгород
                    DeliveryCard(
                        title = "Межгород",
                        enabled = s.intercityEnabled,
                        onToggle = { on ->
                            viewModel.updateLocal { it.copy(intercityEnabled = on) }
                        }
                    ) {
                        if (s.intercityEnabled) {
                            Text(
                                "Клиент увидит: «Межгород: есть». Дальше вы договоритесь в чате.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Подсказка: можно отправлять через логистику (Kaspi / CDEK / Boxberry и т.д.).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            SaveCancelRow(
                                onSave = { viewModel.saveIntercity { snackbar = it } },
                                onCancel = { viewModel.revert() }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }

                // если надо — покажем одну простую подсказку
                snackbar?.let { msg ->
                    // без сложных SnackbarHostState — просто текстом сверху/снизу
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun DeliveryCard(
    title: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Switch(checked = enabled, onCheckedChange = onToggle)
            }
            Spacer(Modifier.height(6.dp))
            content()
        }
    }
}

@Composable
private fun SaveCancelRow(
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(onClick = onSave, modifier = Modifier.weight(1f)) { Text("Сохранить") }
        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Отмена") }
    }
}