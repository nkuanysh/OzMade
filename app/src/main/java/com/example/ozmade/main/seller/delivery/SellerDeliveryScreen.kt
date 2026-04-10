package com.example.ozmade.main.seller.delivery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
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
fun SellerDeliveryRoute(
    onSuccess: () -> Unit,
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
            TopAppBar(
                title = {
                    Text(
                        "Настройки доставки",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState is SellerDeliveryUiState.Data) {
                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = {
                            viewModel.saveAll(
                                onSuccess = onSuccess,
                                onError = { msg ->
                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Сохранить изменения",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            when (val st = uiState) {
                is SellerDeliveryUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeWidth = 3.dp)
                    }
                }

                is SellerDeliveryUiState.Error -> {
                    ErrorContent(message = st.message, onRetry = { viewModel.load() })
                }

                is SellerDeliveryUiState.Data -> {
                    val s = st.ui
                    val scroll = rememberScrollState()

                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        InfoCard(
                            text = "Настройте способы, которыми вы готовы передавать товары покупателям. Активные способы будут отображаться в ваших товарах."
                        )

                        DeliveryCard(
                            title = "Самовывоз",
                            description = "Покупатель сам забирает товар по вашему адресу",
                            icon = Icons.Default.Storefront,
                            enabled = s.pickupEnabled,
                            onToggle = { on -> viewModel.updateLocal { it.copy(pickupEnabled = on) } }
                        ) {
                            ModernTextField(
                                value = s.pickupAddress,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(pickupAddress = v) } },
                                label = "Адрес пункта выдачи",
                                icon = Icons.Outlined.LocationOn,
                                placeholder = "г. Алматы, ул. Абая 10, оф. 5"
                            )
                            Spacer(Modifier.height(16.dp))
                            ModernTextField(
                                value = s.pickupTime,
                                onValueChange = { v -> viewModel.updateLocal { it.copy(pickupTime = v) } },
                                label = "График работы",
                                icon = Icons.Outlined.Schedule,
                                placeholder = "Пн-Пт: 10:00 - 19:00"
                            )
                        }

                        DeliveryCard(
                            title = "Моя курьерская доставка",
                            description = "Доставка вашими силами в определенном радиусе",
                            icon = Icons.Default.LocalShipping,
                            enabled = s.myDeliveryEnabled,
                            onToggle = { on -> viewModel.updateLocal { it.copy(myDeliveryEnabled = on) } }
                        ) {
                            MyDeliveryMapSection(
                                ui = s,
                                onUiChange = { updated ->
                                    viewModel.updateLocal { updated }
                                },
                                onMessage = { msg ->
                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            )
                        }

                        DeliveryCard(
                            title = "Межгород (ТК)",
                            description = "Доставка через сторонние транспортные компании",
                            icon = Icons.Default.Public,
                            enabled = s.intercityEnabled,
                            onToggle = { on -> viewModel.updateLocal { it.copy(intercityEnabled = on) } }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        Icons.Outlined.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Покупатели из других регионов увидят возможность доставки ТК. Вы сможете обсудить детали отправки в чате.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MyDeliveryMapSection(
    ui: SellerDeliveryUi,
    onUiChange: (SellerDeliveryUi) -> Unit,
    onMessage: (String) -> Unit
) {
    var isFullScreenMapOpen by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Выберите центр зоны доставки на карте",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            text = "Нажмите на карту, чтобы выбрать точку. Адрес определится автоматически, а круг покажет зону доставки.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )

        DeliveryMiniMap(
            ui = ui,
            onUiChange = onUiChange,
            onMessage = onMessage,
            onExpandClick = { isFullScreenMapOpen = true }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { isFullScreenMapOpen = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.OpenInFull, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Открыть карту на весь экран")
            }
        }

        ModernTextField(
            value = ui.centerAddress,
            onValueChange = { v -> onUiChange(ui.copy(centerAddress = v)) },
            label = "Адрес центра доставки",
            icon = Icons.Outlined.LocationOn,
            placeholder = "Определится после выбора точки на карте"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Радиус покрытия",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${ui.radiusKm} км",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Slider(
                value = ui.radiusKm.toFloat(),
                onValueChange = { value ->
                    onUiChange(ui.copy(radiusKm = value.toInt().coerceIn(1, 100)))
                },
                valueRange = 1f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            )

            Text(
                "Круг на карте обновляется сразу. Эти значения сохранятся в существующие поля centerLat, centerLng, radiusKm и centerAddress.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }

        val lat = ui.centerLat.toDoubleOrNull()
        val lng = ui.centerLng.toDoubleOrNull()

        if (lat != null && lng != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Координаты центра",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "lat: $lat\nlng: $lng",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    if (isFullScreenMapOpen) {
        FullScreenDeliveryMapDialog(
            ui = ui,
            onDismiss = { isFullScreenMapOpen = false },
            onUiChange = onUiChange,
            onMessage = onMessage
        )
    }
}

@Composable
private fun DeliveryMiniMap(
    ui: SellerDeliveryUi,
    onUiChange: (SellerDeliveryUi) -> Unit,
    onMessage: (String) -> Unit,
    onExpandClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val initialLat = ui.centerLat.toDoubleOrNull()
    val initialLng = ui.centerLng.toDoubleOrNull()
    val selectedLatLng = if (initialLat != null && initialLng != null) {
        LatLng(initialLat, initialLng)
    } else {
        DEFAULT_ALMATY
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedLatLng,
            if (initialLat != null && initialLng != null) 12f else 10f
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(18.dp)
            )
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
            onMapClick = { latLng ->
                scope.launch {
                    val resolvedAddress = reverseGeocode(context, latLng)
                    onUiChange(
                        ui.copy(
                            centerLat = latLng.latitude.toString(),
                            centerLng = latLng.longitude.toString(),
                            centerAddress = resolvedAddress
                        )
                    )
                    if (resolvedAddress.isBlank()) {
                        onMessage("Точка выбрана, но адрес определить не удалось")
                    }
                }
            }
        ) {
            DeliveryMapContent(ui = ui)
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Тап по карте",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FullScreenDeliveryMapDialog(
    ui: SellerDeliveryUi,
    onDismiss: () -> Unit,
    onUiChange: (SellerDeliveryUi) -> Unit,
    onMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val hasLocationPermission = rememberLocationPermissionState()
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!granted) onMessage("Разрешение на геолокацию не выдано")
    }

    val initialLat = ui.centerLat.toDoubleOrNull()
    val initialLng = ui.centerLng.toDoubleOrNull()
    val selectedLatLng = if (initialLat != null && initialLng != null) {
        LatLng(initialLat, initialLng)
    } else {
        DEFAULT_ALMATY
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedLatLng,
            if (initialLat != null && initialLng != null) 13f else 11f
        )
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
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = true,
                    mapToolbarEnabled = false
                ),
                onMapClick = { latLng ->
                    scope.launch {
                        val resolvedAddress = reverseGeocode(context, latLng)
                        onUiChange(
                            ui.copy(
                                centerLat = latLng.latitude.toString(),
                                centerLng = latLng.longitude.toString(),
                                centerAddress = resolvedAddress
                            )
                        )
                        if (resolvedAddress.isBlank()) {
                            onMessage("Точка выбрана, но адрес определить не удалось")
                        }
                    }
                }
            ) {
                DeliveryMapContent(ui = ui)
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
                        if (myLatLng == null) {
                            onMessage("Не удалось определить текущее местоположение")
                            return@launch
                        }

                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(myLatLng, 15f),
                            durationMs = 700
                        )
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
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Выберите центр зоны доставки",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Text(
                        text = ui.centerAddress.ifBlank { "Нажмите на карту, чтобы выбрать точку" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                onUiChange(
                                    ui.copy(
                                        centerLat = "",
                                        centerLng = "",
                                        centerAddress = ""
                                    )
                                )
                                onMessage("Точка доставки очищена")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Очистить")
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
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
}

@Composable
private fun DeliveryMapContent(ui: SellerDeliveryUi) {
    val currentLat = ui.centerLat.toDoubleOrNull()
    val currentLng = ui.centerLng.toDoubleOrNull()

    if (currentLat != null && currentLng != null) {
        val center = LatLng(currentLat, currentLng)

        Marker(
            state = MarkerState(position = center),
            title = "Центр доставки",
            snippet = ui.centerAddress.ifBlank { "Выбранная точка" }
        )

        Circle(
            center = center,
            radius = ui.radiusKm.toDouble() * 1000.0,
            fillColor = Color(0x223B82F6),
            strokeColor = Color(0xFF3B82F6),
            strokeWidth = 4f
        )
    }
}

@Composable
private fun rememberLocationPermissionState(): Boolean {
    val context = LocalContext.current
    return remember {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        fine || coarse
    }
}

@Composable
private fun InfoCard(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = icon?.let {
                { Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun DeliveryCard(
    title: String,
    description: String,
    icon: ImageVector,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.surfaceVariant,
        label = "bgColor"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        label = "borderColor"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.6f,
        label = "alpha"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        border = BorderStroke(1.dp, animatedBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 6.dp else 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .graphicsLayer(alpha = contentAlpha)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (enabled) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        uncheckedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            AnimatedVisibility(
                visible = enabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        thickness = 0.5.dp
                    )
                    Spacer(Modifier.height(20.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.errorContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Произошла ошибка",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Попробовать снова")
        }
    }
}

private suspend fun getCurrentLocationLatLng(context: Context): LatLng? {
    return try {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fine && !coarse) return null

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