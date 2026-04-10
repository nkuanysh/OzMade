package com.example.ozmade.main.user.orderflow.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.orders.data.DeliveryType
import com.example.ozmade.main.user.orderflow.data.DeliveryChooseViewModel2
import com.example.ozmade.main.userHome.details.ProductDetailsUi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

private val DEFAULT_ALMATY = LatLng(43.238949, 76.889709)

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selected by remember { mutableStateOf<String?>(null) }
    var shippingAddressText by remember { mutableStateOf("") }
    var shippingLat by remember { mutableStateOf<Double?>(null) }
    var shippingLng by remember { mutableStateOf<Double?>(null) }
    var shippingComment by remember { mutableStateOf("") }
    var isFullScreenMapOpen by remember { mutableStateOf(false) }

    val total = product.price * quantity
    val d = product.delivery

    val zoneCenter = remember(d.centerLat, d.centerLng) {
        if (d.centerLat != null && d.centerLng != null) {
            LatLng(d.centerLat, d.centerLng)
        } else {
            DEFAULT_ALMATY
        }
    }

    val zoneRadiusKm = d.radiusKm ?: 0.0

    val buyerLatLng = remember(shippingLat, shippingLng) {
        if (shippingLat != null && shippingLng != null) {
            LatLng(shippingLat!!, shippingLng!!)
        } else null
    }

    val distanceKm: Double? = remember(buyerLatLng, d.centerLat, d.centerLng) {
        if (buyerLatLng != null && d.centerLat != null && d.centerLng != null) {
            val result = FloatArray(1)
            Location.distanceBetween(
                d.centerLat,
                d.centerLng,
                buyerLatLng.latitude,
                buyerLatLng.longitude,
                result
            )
            result[0].toDouble() / 1000.0
        } else {
            null
        }
    }

    val isOutsideSellerZone = remember(distanceKm, zoneRadiusKm) {
        distanceKm?.let { zoneRadiusKm > 0.0 && it > zoneRadiusKm } ?: false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(product.title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            "Сумма к оплате: $total ₸",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

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
                subtitle = buildString {
                    append("Зона: ${d.centerAddress ?: "—"}")
                    if (d.radiusKm != null) append("\nРадиус: ${formatKm(d.radiusKm)} км")
                },
                selected = selected == DeliveryType.MY_DELIVERY,
                onClick = { selected = DeliveryType.MY_DELIVERY }
            )

            AnimatedVisibility(visible = selected == DeliveryType.MY_DELIVERY) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(
                        "Выберите ваш адрес на карте. Покупатель видит зону доставки продавца.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(8.dp))

                    BuyerMiniMap(
                        zoneCenter = zoneCenter,
                        zoneRadiusKm = zoneRadiusKm,
                        buyerLatLng = buyerLatLng,
                        onExpandClick = { isFullScreenMapOpen = true },
                        onMapClick = { latLng ->
                            shippingLat = latLng.latitude
                            shippingLng = latLng.longitude
                            scope.launch {
                                val address = reverseGeocode(context, latLng)
                                if (address.isNotBlank()) {
                                    shippingAddressText = address
                                }
                            }
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { isFullScreenMapOpen = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.OpenInFull, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Открыть карту на весь экран")
                    }

                    Spacer(Modifier.height(12.dp))

                    if (distanceKm != null) {
                        InfoSurface(
                            text = if (isOutsideSellerZone) {
                                "Этот адрес находится вне зоны продавца. Радиус продавца: ${formatKm(zoneRadiusKm)} км, расстояние до адреса: ${formatKm(distanceKm)} км. Возможно, потребуется доплата за доставку."
                            } else {
                                "Адрес находится внутри зоны доставки продавца. Расстояние до центра: ${formatKm(distanceKm)} км."
                            },
                            isWarning = isOutsideSellerZone
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = shippingAddressText,
                        onValueChange = { shippingAddressText = it },
                        label = { Text("Точный адрес") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
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
                    text = mapDeliveryError(error),
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
                    selected != DeliveryType.MY_DELIVERY || (
                            shippingLat != null &&
                                    shippingLng != null &&
                                    shippingAddressText.isNotBlank()
                            )
                    ) && (
                    selected != DeliveryType.INTERCITY || shippingAddressText.isNotBlank()
                    ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (saving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Подтвердить заказ", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(32.dp))
    }

    if (isFullScreenMapOpen && selected == DeliveryType.MY_DELIVERY) {
        BuyerFullScreenMapDialog(
            zoneCenter = zoneCenter,
            zoneRadiusKm = zoneRadiusKm,
            buyerLatLng = buyerLatLng,
            onDismiss = { isFullScreenMapOpen = false },
            onMapClick = { latLng ->
                shippingLat = latLng.latitude
                shippingLng = latLng.longitude
                scope.launch {
                    val address = reverseGeocode(context, latLng)
                    if (address.isNotBlank()) {
                        shippingAddressText = address
                    }
                }
            }
        )
    }
}

@Composable
private fun BuyerMiniMap(
    zoneCenter: LatLng,
    zoneRadiusKm: Double,
    buyerLatLng: LatLng?,
    onExpandClick: () -> Unit,
    onMapClick: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(zoneCenter, 12f)
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = true,
                mapToolbarEnabled = false
            ),
            onMapClick = onMapClick
        ) {
            SellerZoneAndBuyerMarker(
                zoneCenter = zoneCenter,
                zoneRadiusKm = zoneRadiusKm,
                buyerLatLng = buyerLatLng
            )
        }

        FilledTonalIconButton(
            onClick = onExpandClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(44.dp)
        ) {
            Icon(Icons.Default.OpenInFull, contentDescription = "Открыть карту")
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text("Тап по карте")
            }
        }
    }
}

@Composable
private fun BuyerFullScreenMapDialog(
    zoneCenter: LatLng,
    zoneRadiusKm: Double,
    buyerLatLng: LatLng?,
    onDismiss: () -> Unit,
    onMapClick: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasLocationPermission by remember { mutableStateOf(checkLocationPermission(context)) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission =
            result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(zoneCenter, 13f)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = true,
                    mapToolbarEnabled = false
                ),
                onMapClick = onMapClick
            ) {
                SellerZoneAndBuyerMarker(
                    zoneCenter = zoneCenter,
                    zoneRadiusKm = zoneRadiusKm,
                    buyerLatLng = buyerLatLng
                )
            }

            FilledTonalIconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(48.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }

            FilledTonalIconButton(
                onClick = {
                    scope.launch {
                        if (!hasLocationPermission) {
                            requestPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                            return@launch
                        }

                        val myLatLng = getCurrentLocationLatLng(context)
                        if (myLatLng != null) {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(myLatLng, 15f),
                                durationMs = 700
                            )
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Моё местоположение")
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 10.dp,
                shadowElevation = 10.dp,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Выберите ваш адрес",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(12.dp))

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Готово")
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerZoneAndBuyerMarker(
    zoneCenter: LatLng,
    zoneRadiusKm: Double,
    buyerLatLng: LatLng?
) {
    Marker(
        state = MarkerState(position = zoneCenter),
        title = "Зона продавца"
    )

    if (zoneRadiusKm > 0) {
        Circle(
            center = zoneCenter,
            radius = zoneRadiusKm * 1000.0,
            fillColor = Color(0x223B82F6),
            strokeColor = Color(0xFF3B82F6),
            strokeWidth = 4f
        )
    }

    if (buyerLatLng != null) {
        Marker(
            state = MarkerState(position = buyerLatLng),
            title = "Ваш адрес"
        )
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
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(selected = selected, onClick = onClick)
        }
    }
}

@Composable
private fun InfoSurface(
    text: String,
    isWarning: Boolean
) {
    Surface(
        color = if (isWarning) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            color = if (isWarning) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.onPrimaryContainer
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun mapDeliveryError(error: String): String {
    return when {
        error.contains("Delivery is not available for this address", ignoreCase = true) ->
            "Доставка по этому адресу недоступна. Выберите адрес внутри зоны продавца или другой способ доставки."
        else -> error
    }
}

private fun formatKm(value: Double): String {
    return String.format(Locale.US, "%.2f", value)
}

private fun checkLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fine || coarse
}

private suspend fun getCurrentLocationLatLng(context: Context): LatLng? {
    return try {
        if (!checkLocationPermission(context)) return null
        val client = LocationServices.getFusedLocationProviderClient(context)
        val location = client.lastLocation.await() ?: return null
        LatLng(location.latitude, location.longitude)
    } catch (_: Exception) {
        null
    }
}

private suspend fun reverseGeocode(context: Context, latLng: LatLng): String {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale("ru", "KZ"))

            val addresses: List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { result ->
                        continuation.resume(result ?: emptyList())
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).orEmpty()
            }

            val address = addresses.firstOrNull()
            when {
                address == null -> ""
                !address.getAddressLine(0).isNullOrBlank() -> address.getAddressLine(0)
                else -> buildString {
                    listOfNotNull(
                        address.locality,
                        address.thoroughfare,
                        address.subThoroughfare
                    ).forEachIndexed { index, part ->
                        if (index > 0) append(", ")
                        append(part)
                    }
                }
            }
        } catch (_: Exception) {
            ""
        }
    }
}