package com.example.ozmade.main.user.orderflow.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.orders.data.DeliveryType
import com.example.ozmade.main.user.orderflow.data.DeliveryChooseViewModel2
import com.example.ozmade.main.userHome.details.ProductDetailsUi
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

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

    LaunchedEffect(productId) {
        viewModel.load(productId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Способ доставки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        when (val st = state) {
            is DeliveryChooseViewModel2.UiState.Loading -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DeliveryChooseViewModel2.UiState.Error -> {
                Column(Modifier.padding(padding).padding(16.dp)) {
                    Text(st.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.load(productId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Повторить")
                    }
                }
            }

            is DeliveryChooseViewModel2.UiState.Data -> {
                DeliveryChooseContent(
                    modifier = Modifier.padding(padding),
                    product = st.product,
                    quantity = quantity,
                    saving = st.saving,
                    error = st.actionError,
                    onCreate = { deliveryType, address, lat, lng, comment ->
                        viewModel.createOrder(
                            productId = productId,
                            quantity = quantity,
                            deliveryType = deliveryType,
                            shippingAddressText = address,
                            shippingLat = lat,
                            shippingLng = lng,
                            shippingComment = comment,
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
    modifier: Modifier = Modifier,
    product: ProductDetailsUi,
    quantity: Int,
    saving: Boolean,
    error: String?,
    onCreate: (String, String?, Double?, Double?, String?) -> Unit
) {
    var selected by remember { mutableStateOf<String?>(null) }
    var shippingAddressText by remember { mutableStateOf("") }
    var shippingLat by remember { mutableStateOf<Double?>(null) }
    var shippingLng by remember { mutableStateOf<Double?>(null) }
    var shippingComment by remember { mutableStateOf("") }

    val total = product.price * quantity

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(product.title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text("Сумма к оплате: $total ₸", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

        Spacer(Modifier.height(24.dp))

        val d = product.delivery

        if (d.pickupEnabled) {
            DeliveryOption(
                title = "Самовывоз",
                subtitle = "Адрес: ${d.pickupAddress ?: "—"}\nВремя: ${d.pickupTime ?: "—"}",
                selected = selected == DeliveryType.PICKUP,
                onClick = { selected = DeliveryType.PICKUP }
            )
            Spacer(Modifier.height(12.dp))
        }

        if (d.freeDeliveryEnabled) {
            DeliveryOption(
                title = "Доставка продавца",
                subtitle = "Зона: ${d.centerAddress ?: "—"}\nРадиус: ${d.radiusKm ?: 0} км",
                selected = selected == DeliveryType.MY_DELIVERY,
                onClick = { selected = DeliveryType.MY_DELIVERY }
            )
            
            AnimatedVisibility(visible = selected == DeliveryType.MY_DELIVERY) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text("Укажите ваше местоположение на карте", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    
                    val zoneCenter = LatLng(d.centerLat ?: 43.238, d.centerLng ?: 76.889)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(zoneCenter, 12f)
                    }
                    
                    Box(Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(12.dp))) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { latLng ->
                                shippingLat = latLng.latitude
                                shippingLng = latLng.longitude
                            }
                        ) {
                            // Draw seller zone
                            Circle(
                                center = zoneCenter,
                                radius = (d.radiusKm ?: 0.0) * 1000.0,
                                fillColor = Color.Blue.copy(alpha = 0.1f),
                                strokeColor = Color.Blue.copy(alpha = 0.5f),
                                strokeWidth = 2f
                            )
                            
                            // Draw buyer marker
                            if (shippingLat != null && shippingLng != null) {
                                Marker(
                                    state = MarkerState(position = LatLng(shippingLat!!, shippingLng!!)),
                                    title = "Ваш адрес"
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = shippingAddressText,
                        onValueChange = { shippingAddressText = it },
                        label = { Text("Точный адрес (улица, дом, кв)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = shippingComment,
                        onValueChange = { shippingComment = it },
                        label = { Text("Комментарий для курьера") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (d.intercityEnabled) {
            DeliveryOption(
                title = "Межгород",
                subtitle = "Доставка транспортными компаниями",
                selected = selected == DeliveryType.INTERCITY,
                onClick = { selected = DeliveryType.INTERCITY }
            )

            AnimatedVisibility(visible = selected == DeliveryType.INTERCITY) {
                Column(Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = shippingAddressText,
                        onValueChange = { shippingAddressText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Полный адрес (Город, улица, дом)") },
                        minLines = 2
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (!error.isNullOrBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val type = selected ?: return@Button
                onCreate(
                    type,
                    shippingAddressText.trim().ifBlank { null },
                    shippingLat,
                    shippingLng,
                    shippingComment.trim().ifBlank { null }
                )
            },
            enabled = !saving && selected != null && (
                selected != DeliveryType.MY_DELIVERY || (shippingLat != null && shippingAddressText.isNotBlank())
            ) && (
                selected != DeliveryType.INTERCITY || shippingAddressText.isNotBlank()
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (saving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            else Text("Подтвердить заказ", style = MaterialTheme.typography.titleMedium)
        }
        
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun DeliveryOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            RadioButton(selected = selected, onClick = onClick)
        }
    }
}
