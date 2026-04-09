package com.example.ozmade.main.seller.delivery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDeliveryRoute(
    onBack: () -> Unit,
    viewModel: SellerDeliveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Настройки доставки", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState is SellerDeliveryUiState.Data) {
                Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
                    Button(
                        onClick = {
                            viewModel.savePickup { scope.launch { snackbarHostState.showSnackbar(it) } }
                            viewModel.saveMyDelivery { scope.launch { snackbarHostState.showSnackbar(it) } }
                            viewModel.saveIntercity { scope.launch { snackbarHostState.showSnackbar(it) } }
                        },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Сохранить все изменения")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
            when (val st = uiState) {
                is SellerDeliveryUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is SellerDeliveryUiState.Error -> Column(Modifier.align(Alignment.Center).padding(16.dp)) {
                    Text(st.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.load() }) { Text("Повторить") }
                }
                is SellerDeliveryUiState.Data -> {
                    val s = st.ui
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DeliveryCard(
                            title = "Самовывоз",
                            description = "Покупатель забирает товар сам",
                            icon = Icons.Default.Storefront,
                            enabled = s.pickupEnabled,
                            onToggle = { on -> viewModel.updateLocal { it.copy(pickupEnabled = on) } }
                        ) {
                            OutlinedTextField(
                                value = s.pickupAddress,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(pickupAddress = v) } },
                                label = { Text("Адрес пункта выдачи") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = s.pickupTime,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(pickupTime = v) } },
                                label = { Text("График работы") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        DeliveryCard(
                            title = "Моя доставка",
                            description = "Зона вашей курьерской доставки",
                            icon = Icons.Default.DeliveryDining,
                            enabled = s.myDeliveryEnabled,
                            onToggle = { on -> viewModel.updateLocal { it.copy(myDeliveryEnabled = on) } }
                        ) {
                            Text("Выберите центр и радиус доставки на карте", style = MaterialTheme.typography.bodySmall)
                            
                            val mapCenter = remember(s.centerLat, s.centerLng) {
                                LatLng(s.centerLat ?: 43.238, s.centerLng ?: 76.889)
                            }
                            val cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(mapCenter, 12f)
                            }

                            Box(Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(12.dp))) {
                                GoogleMap(
                                    modifier = Modifier.fillMaxSize(),
                                    cameraPositionState = cameraPositionState,
                                    onMapClick = { latLng ->
                                        viewModel.updateLocal { it.copy(centerLat = latLng.latitude, centerLng = latLng.longitude) }
                                    }
                                ) {
                                    if (s.centerLat != null && s.centerLng != null) {
                                        val pos = LatLng(s.centerLat, s.centerLng)
                                        Marker(state = MarkerState(position = pos), title = "Центр доставки")
                                        com.google.maps.android.compose.Circle(
                                            center = pos,
                                            radius = s.radiusKm * 1000.0,
                                            fillColor = Color.Blue.copy(alpha = 0.2f),
                                            strokeColor = Color.Blue,
                                            strokeWidth = 2f
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            Text("Радиус: ${s.radiusKm} км")
                            Slider(
                                value = s.radiusKm.toFloat(),
                                onValueChange = { v -> viewModel.updateLocal { it.copy(radiusKm = v.toInt()) } },
                                valueRange = 1f..50f
                            )
                            OutlinedTextField(
                                value = s.centerAddress,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(centerAddress = v) } },
                                label = { Text("Описание зоны (напр. город Алматы)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        DeliveryCard(
                            title = "Межгород",
                            description = "Доставка сторонними службами",
                            icon = Icons.Default.Public,
                            enabled = s.intercityEnabled,
                            onToggle = { on -> viewModel.updateLocal { it.copy(intercityEnabled = on) } }
                        ) {
                            Text("Позволяет покупателям из других городов заказывать ваши товары.")
                        }
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryCard(title: String, description: String, icon: ImageVector, enabled: Boolean, onToggle: (Boolean) -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(description, style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = enabled, onCheckedChange = onToggle)
            }
            AnimatedVisibility(visible = enabled) {
                Column { Spacer(Modifier.height(16.dp)); content() }
            }
        }
    }
}
