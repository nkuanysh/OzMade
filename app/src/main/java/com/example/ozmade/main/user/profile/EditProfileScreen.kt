package com.example.ozmade.main.user.profile

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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ozmade.main.user.profile.data.EditProfileViewModel
import com.example.ozmade.main.user.profile.data.EditProfileState
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
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.onPhotoPicked(it) }
        }
    )

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Мои данные", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.clickable {
                    photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    if (state.selectedUri != null || state.photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = state.selectedUri ?: state.photoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 4.dp
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            EditFieldCard {
                CustomEditTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Как вас зовут?",
                    placeholder = "Введите имя"
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp,
                    color = Color(0xFFEEEEEE)
                )

                CustomEditTextField(
                    value = state.address,
                    onValueChange = viewModel::onAddressChange,
                    label = "Ваш адрес",
                    placeholder = "Выберите адрес на карте"
                )

                Spacer(Modifier.height(12.dp))

                ProfileAddressMapSection(
                    state = state,
                    onAddressPicked = { address, lat, lng ->
                        viewModel.onAddressPicked(address, lat, lng)
                    },
                    onClear = viewModel::clearPickedAddress
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp,
                    color = Color(0xFFEEEEEE)
                )
            }

            AnimatedVisibility(visible = state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (state.loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { viewModel.save(onSuccess = onBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.loading && !state.saving,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                if (state.saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp,
                        color = Color.White
                    )
                } else {
                    Text("Сохранить изменения", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileAddressMapSection(
    state: EditProfileState,
    onAddressPicked: (String, Double, Double) -> Unit,
    onClear: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isFullScreenMapOpen by remember { mutableStateOf(false) }

    val selectedLatLng = remember(state.addressLat, state.addressLng) {
        if (state.addressLat != null && state.addressLng != null) {
            LatLng(state.addressLat, state.addressLng)
        } else {
            DEFAULT_ALMATY
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedLatLng,
            if (state.addressLat != null && state.addressLng != null) 14f else 10f
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Выберите адрес на карте",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
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
                        val address = reverseGeocode(context, latLng)
                        onAddressPicked(
                            address.ifBlank { "${latLng.latitude}, ${latLng.longitude}" },
                            latLng.latitude,
                            latLng.longitude
                        )
                    }
                }
            ) {
                if (state.addressLat != null && state.addressLng != null) {
                    val marker = LatLng(state.addressLat, state.addressLng)
                    Marker(
                        state = MarkerState(position = marker),
                        title = "Ваш адрес",
                        snippet = state.address.ifBlank { "Сохранённый адрес" }
                    )
                }
            }

            FilledTonalIconButton(
                onClick = { isFullScreenMapOpen = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(44.dp)
            ) {
                Icon(Icons.Default.OpenInFull, contentDescription = null)
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Тап по карте")
                }
            }
        }

        OutlinedButton(
            onClick = { isFullScreenMapOpen = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.OpenInFull, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Открыть карту на весь экран")
        }

        if (state.addressLat != null && state.addressLng != null) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Очистить адрес")
            }
        }
    }

    if (isFullScreenMapOpen) {
        FullScreenProfileAddressMapDialog(
            selectedAddress = state.address,
            selectedLat = state.addressLat,
            selectedLng = state.addressLng,
            onDismiss = { isFullScreenMapOpen = false },
            onAddressPicked = onAddressPicked
        )
    }
}

@Composable
private fun FullScreenProfileAddressMapDialog(
    selectedAddress: String,
    selectedLat: Double?,
    selectedLng: Double?,
    onDismiss: () -> Unit,
    onAddressPicked: (String, Double, Double) -> Unit
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

    val selectedLatLng = if (selectedLat != null && selectedLng != null) {
        LatLng(selectedLat, selectedLng)
    } else {
        DEFAULT_ALMATY
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedLatLng,
            if (selectedLat != null && selectedLng != null) 15f else 11f
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
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
                onMapClick = { latLng ->
                    scope.launch {
                        val address = reverseGeocode(context, latLng)
                        onAddressPicked(
                            address.ifBlank { "${latLng.latitude}, ${latLng.longitude}" },
                            latLng.latitude,
                            latLng.longitude
                        )
                    }
                }
            ) {
                if (selectedLat != null && selectedLng != null) {
                    Marker(
                        state = MarkerState(position = LatLng(selectedLat, selectedLng)),
                        title = "Ваш адрес",
                        snippet = selectedAddress.ifBlank { "Сохранённый адрес" }
                    )
                }
            }

            FilledTonalIconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(48.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
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
                                CameraUpdateFactory.newLatLngZoom(myLatLng, 16f),
                                700
                            )
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null)
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 10.dp,
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedAddress.ifBlank { "Нажмите на карту, чтобы выбрать адрес" },
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = onDismiss) {
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
private fun EditFieldCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun CustomEditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
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