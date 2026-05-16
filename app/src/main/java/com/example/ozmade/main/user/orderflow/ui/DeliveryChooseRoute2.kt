package com.example.ozmade.main.user.orderflow.ui

import com.example.ozmade.R
import androidx.compose.ui.res.stringResource
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
import androidx.compose.material.icons.filled.LocalShipping
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
import com.example.ozmade.main.delivery.extractCity
import com.example.ozmade.main.delivery.formatDeliveryDateRange
import com.example.ozmade.main.delivery.formatDeliveryPrice
import com.example.ozmade.main.orders.data.DeliveryType
import com.example.ozmade.main.user.orderflow.data.DeliveryChooseViewModel2
import com.example.ozmade.main.userHome.details.ProductDetailsUi
import com.example.ozmade.network.model.DeliveryAddressRequest
import com.example.ozmade.network.model.DeliveryPackageRequest
import com.example.ozmade.network.model.IntercityDeliveryOrderRequest
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
                title = { Text(stringResource(R.string.choose_delivery_method)) },
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
                        Text(stringResource(R.string.retry_btn))
                    }
                }
            }

            is DeliveryChooseViewModel2.UiState.Data -> {
                DeliveryChooseContent(
                    modifier = Modifier.padding(padding),
                    product = st.product,
                    quantity = quantity,
                    buyerName = st.buyerName,
                    buyerPhone = st.buyerPhone,
                    saving = st.saving,
                    error = st.actionError,
                    intercityEstimate = st.intercityEstimate,
                    onEstimateIntercity = viewModel::estimateIntercityDelivery,
                    onCreate = { deliveryType, address, lat, lng, comment, intercityDelivery ->
                        viewModel.createOrder(
                            productId = productId,
                            quantity = quantity,
                            deliveryType = deliveryType,
                            shippingAddressText = address,
                            shippingLat = lat,
                            shippingLng = lng,
                            shippingComment = comment,
                            intercityDelivery = intercityDelivery,
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
    buyerName: String,
    buyerPhone: String,
    saving: Boolean,
    error: String?,
    intercityEstimate: DeliveryChooseViewModel2.IntercityEstimateState,
    onEstimateIntercity: (String?) -> Unit,
    onCreate: (String, String?, Double?, Double?, String?, IntercityDeliveryOrderRequest?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selected by remember { mutableStateOf<String?>(null) }
    var shippingAddressText by remember { mutableStateOf("") }
    var shippingLat by remember { mutableStateOf<Double?>(null) }
    var shippingLng by remember { mutableStateOf<Double?>(null) }
    var shippingComment by remember { mutableStateOf("") }
    var receiverName by remember(buyerName) { mutableStateOf(buyerName) }
    var receiverPhone by remember(buyerPhone) { mutableStateOf(buyerPhone) }
    var isFullScreenMapOpen by remember { mutableStateOf(false) }
    var localValidationError by remember { mutableStateOf<String?>(null) }
    val total = product.price * quantity
    val d = product.delivery
    LaunchedEffect(d.buyerSavedAddress, d.buyerSavedAddressLat, d.buyerSavedAddressLng, selected) {
        if (
            (selected == DeliveryType.MY_DELIVERY || selected == DeliveryType.INTERCITY) &&
            shippingAddressText.isBlank() &&
            shippingLat == null &&
            shippingLng == null &&
            !d.buyerSavedAddress.isNullOrBlank() &&
            d.buyerSavedAddressLat != null &&
            d.buyerSavedAddressLng != null
        ) {
            shippingAddressText = d.buyerSavedAddress
            shippingLat = d.buyerSavedAddressLat
            shippingLng = d.buyerSavedAddressLng
        }
    }

    LaunchedEffect(selected, shippingAddressText) {
        if (selected == DeliveryType.INTERCITY) {
            onEstimateIntercity(shippingAddressText)
        }
    }
    val sellerZoneAddress = d.centerAddress?.trim().orEmpty()
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
            stringResource(R.string.amount_to_pay, total),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        if (d.pickupEnabled) {
            DeliveryOption(
                title = stringResource(R.string.delivery_type_pickup),
                subtitle = stringResource(R.string.address) + ": ${d.pickupAddress ?: stringResource(R.string.dash)}\n" + stringResource(R.string.working_hours) + ": ${d.pickupTime ?: stringResource(R.string.dash)}",
                selected = selected == DeliveryType.PICKUP,
                onClick = { selected = DeliveryType.PICKUP }
            )
            Spacer(Modifier.height(12.dp))
        }

        if (d.freeDeliveryEnabled) {
            DeliveryOption(
                title = stringResource(R.string.delivery_method_seller),
                subtitle = buildString {
                    append(stringResource(R.string.seller_address_value, sellerZoneAddress.ifBlank { stringResource(R.string.no_address) }))
                    if (d.radiusKm != null) append("\n" + stringResource(R.string.radius_value, formatKm(d.radiusKm)))
                },
                selected = selected == DeliveryType.MY_DELIVERY,
                onClick = {
                    selected = DeliveryType.MY_DELIVERY
                    if (
                        shippingAddressText.isBlank() &&
                        shippingLat == null &&
                        shippingLng == null &&
                        !d.buyerSavedAddress.isNullOrBlank() &&
                        d.buyerSavedAddressLat != null &&
                        d.buyerSavedAddressLng != null
                    ) {
                        shippingAddressText = d.buyerSavedAddress
                        shippingLat = d.buyerSavedAddressLat
                        shippingLng = d.buyerSavedAddressLng
                    }
                }
            )

            AnimatedVisibility(visible = selected == DeliveryType.MY_DELIVERY) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(
                        stringResource(R.string.choose_buyer_address_map),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(8.dp))

                    BuyerMiniMap(
                        zoneCenter = zoneCenter,
                        zoneRadiusKm = zoneRadiusKm,
                        sellerZoneAddress = sellerZoneAddress,
                        buyerLatLng = buyerLatLng,
                        onExpandClick = { isFullScreenMapOpen = true },
                        onMapClick = { latLng ->
                            localValidationError = null
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
                        Text(stringResource(R.string.edit_profile_full_screen_map))
                    }
                    Spacer(Modifier.height(12.dp))

                    InfoSurface(
                        text = stringResource(R.string.seller_zone_address_value, sellerZoneAddress.ifBlank { stringResource(R.string.no_address) }),
                        isWarning = false
                    )

                    Spacer(Modifier.height(12.dp))

                    if (distanceKm != null) {
                        InfoSurface(
                            text = if (isOutsideSellerZone) {
                                stringResource(R.string.outside_seller_zone_warning, formatKm(zoneRadiusKm), formatKm(distanceKm))
                            } else {
                                stringResource(R.string.inside_seller_zone_info, formatKm(distanceKm))
                            },
                            isWarning = isOutsideSellerZone
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = shippingAddressText,
                        onValueChange = { shippingAddressText = it },
                        label = { Text(stringResource(R.string.exact_address)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = shippingComment,
                        onValueChange = { shippingComment = it },
                        label = { Text(stringResource(R.string.courier_comment)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        if (d.intercityEnabled) {
            DeliveryOption(
                title = stringResource(R.string.delivery_type_intercity),
                subtitle = stringResource(R.string.delivery_transport_companies),
                selected = selected == DeliveryType.INTERCITY,
                onClick = {
                    selected = DeliveryType.INTERCITY
                    if (
                        shippingAddressText.isBlank() &&
                        shippingLat == null &&
                        shippingLng == null &&
                        !d.buyerSavedAddress.isNullOrBlank()
                    ) {
                        shippingAddressText = d.buyerSavedAddress
                        shippingLat = d.buyerSavedAddressLat
                        shippingLng = d.buyerSavedAddressLng
                    }
                }
            )

            AnimatedVisibility(visible = selected == DeliveryType.INTERCITY) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoSurface(
                        text = stringResource(
                            R.string.ship_from_city,
                            d.sellerPickupCity ?: product.seller.address.ifBlank { stringResource(R.string.no_address) }
                        ),
                        isWarning = d.sellerPickupCity.isNullOrBlank()
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                stringResource(R.string.delivery_address_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                extractCity(shippingAddressText) ?: stringResource(R.string.no_address),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                shippingAddressText.ifBlank { stringResource(R.string.intercity_no_buyer_address) },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = { isFullScreenMapOpen = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.OpenInFull, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (shippingAddressText.isBlank()) {
                                        stringResource(R.string.choose_address_on_map)
                                    } else {
                                        stringResource(R.string.change_address)
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = shippingAddressText,
                        onValueChange = {
                            shippingAddressText = it
                            shippingLat = null
                            shippingLng = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.full_address_hint)) },
                        minLines = 2
                    )

                    OutlinedTextField(
                        value = receiverName,
                        onValueChange = { receiverName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.receiver_name)) },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = receiverPhone,
                        onValueChange = { receiverPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.receiver_phone)) },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = shippingComment,
                        onValueChange = { shippingComment = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.courier_comment)) },
                        minLines = 2
                    )

                    IntercityEstimateCard(intercityEstimate)
                }
            }

            Spacer(Modifier.height(12.dp))
        }
        if (!localValidationError.isNullOrBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = localValidationError!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(16.dp))
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

                if (type == DeliveryType.MY_DELIVERY) {
                    if (shippingLat == null || shippingLng == null || shippingAddressText.isBlank()) {
                        localValidationError = context.getString(R.string.choose_address_validation)
                        return@Button
                    }

                    if (isOutsideSellerZone) {
                        localValidationError =
                            context.getString(R.string.seller_no_delivery_address)
                        return@Button
                    }
                }

                if (type == DeliveryType.INTERCITY) {
                    if (shippingAddressText.isBlank()) {
                        localValidationError = context.getString(R.string.intercity_no_buyer_address)
                        return@Button
                    }
                    if (intercityEstimate is DeliveryChooseViewModel2.IntercityEstimateState.SameCity) {
                        localValidationError = context.getString(R.string.intercity_same_city_hint)
                        return@Button
                    }
                    if (receiverName.isBlank() || receiverPhone.isBlank()) {
                        localValidationError = context.getString(R.string.receiver_name) + " / " + context.getString(R.string.receiver_phone)
                        return@Button
                    }
                }

                localValidationError = null
                val intercityDeliveryRequest = if (
                    type == DeliveryType.INTERCITY &&
                    intercityEstimate is DeliveryChooseViewModel2.IntercityEstimateState.Success
                ) {
                    buildIntercityDeliveryOrderRequest(
                        product = product,
                        estimateState = intercityEstimate,
                        toAddressText = shippingAddressText.trim(),
                        toLat = shippingLat,
                        toLng = shippingLng,
                        receiverName = receiverName.trim(),
                        receiverPhone = receiverPhone.trim(),
                        comment = shippingComment.trim().ifBlank { null }
                    )
                } else {
                    null
                }

                onCreate(
                    type,
                    shippingAddressText.trim().ifBlank { null },
                    shippingLat,
                    shippingLng,
                    shippingComment.trim().ifBlank { null },
                    intercityDeliveryRequest
                )
            },
            enabled = !saving && selected != null && (
                    selected != DeliveryType.MY_DELIVERY || (
                            shippingLat != null &&
                                    shippingLng != null &&
                                    shippingAddressText.isNotBlank()
                            )
                    ) && (
                    selected != DeliveryType.INTERCITY || (
                            shippingAddressText.isNotBlank() &&
                                    receiverName.isNotBlank() &&
                                    receiverPhone.isNotBlank() &&
                                    intercityEstimate !is DeliveryChooseViewModel2.IntercityEstimateState.Loading &&
                                    intercityEstimate !is DeliveryChooseViewModel2.IntercityEstimateState.SameCity
                            )
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
                Text(stringResource(R.string.confirm_order), style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(32.dp))
    }

    if (isFullScreenMapOpen && (selected == DeliveryType.MY_DELIVERY || selected == DeliveryType.INTERCITY)) {
        BuyerFullScreenMapDialog(
            zoneCenter = zoneCenter,
            zoneRadiusKm = zoneRadiusKm,
            sellerZoneAddress = sellerZoneAddress,
            buyerLatLng = buyerLatLng,
            onDismiss = { isFullScreenMapOpen = false },
            onMapClick = { latLng ->
                localValidationError = null
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
private fun IntercityEstimateCard(
    state: DeliveryChooseViewModel2.IntercityEstimateState
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = when (state) {
                        is DeliveryChooseViewModel2.IntercityEstimateState.Success ->
                            stringResource(R.string.intercity_via_provider, state.estimate.provider)
                        else -> stringResource(R.string.delivery_type_intercity)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            when (state) {
                DeliveryChooseViewModel2.IntercityEstimateState.Idle,
                DeliveryChooseViewModel2.IntercityEstimateState.Loading -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.intercity_calculating))
                    }
                }
                DeliveryChooseViewModel2.IntercityEstimateState.MissingBuyerAddress -> {
                    Text(stringResource(R.string.intercity_no_buyer_address))
                }
                DeliveryChooseViewModel2.IntercityEstimateState.MissingSellerAddress -> {
                    Text(stringResource(R.string.intercity_no_seller_address))
                }
                DeliveryChooseViewModel2.IntercityEstimateState.SameCity -> {
                    Text(stringResource(R.string.intercity_same_city_hint))
                }
                is DeliveryChooseViewModel2.IntercityEstimateState.Error -> {
                    Text(
                        stringResource(R.string.intercity_estimate_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is DeliveryChooseViewModel2.IntercityEstimateState.Success -> {
                    Text(
                        stringResource(R.string.intercity_route, state.fromCity, state.toCity),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(stringResource(R.string.delivery_cost_value, formatDeliveryPrice(state.estimate.price, state.estimate.currency)))
                    Text(stringResource(R.string.delivery_period_value, state.estimate.minDays, state.estimate.maxDays))
                    Text(
                        stringResource(
                            R.string.delivery_estimated_receipt,
                            formatDeliveryDateRange(
                                state.estimate.estimatedDateFrom,
                                state.estimate.estimatedDateTo
                            )
                        )
                    )
                    Text(
                        stringResource(R.string.intercity_preliminary_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun buildIntercityDeliveryOrderRequest(
    product: ProductDetailsUi,
    estimateState: DeliveryChooseViewModel2.IntercityEstimateState.Success,
    toAddressText: String,
    toLat: Double?,
    toLng: Double?,
    receiverName: String,
    receiverPhone: String,
    comment: String?
): IntercityDeliveryOrderRequest {
    val fromAddressText = product.delivery.pickupAddress?.takeIf { it.isNotBlank() }
        ?: product.seller.address
    val packageInfo = product.packageInfo
    return IntercityDeliveryOrderRequest(
        provider = estimateState.estimate.provider,
        price = estimateState.estimate.price,
        currency = estimateState.estimate.currency,
        minDays = estimateState.estimate.minDays,
        maxDays = estimateState.estimate.maxDays,
        estimatedDateFrom = estimateState.estimate.estimatedDateFrom,
        estimatedDateTo = estimateState.estimate.estimatedDateTo,
        fromAddress = DeliveryAddressRequest(
            city = estimateState.fromCity,
            fullAddress = fromAddressText,
            latitude = product.delivery.pickupLat,
            longitude = product.delivery.pickupLng
        ),
        toAddress = DeliveryAddressRequest(
            city = estimateState.toCity,
            fullAddress = toAddressText,
            latitude = toLat,
            longitude = toLng
        ),
        packageInfo = DeliveryPackageRequest(
            weightGrams = packageInfo.weightGrams,
            heightCm = packageInfo.heightCm,
            widthCm = packageInfo.widthCm,
            depthCm = packageInfo.depthCm
        ),
        receiverName = receiverName,
        receiverPhone = receiverPhone,
        receiverAddress = toAddressText,
        comment = comment
    )
}

@Composable
private fun BuyerMiniMap(
    zoneCenter: LatLng,
    zoneRadiusKm: Double,
    sellerZoneAddress: String,
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
                sellerZoneAddress = sellerZoneAddress,
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
            Icon(Icons.Default.OpenInFull, contentDescription = stringResource(R.string.open_map_desc))
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
                Text(stringResource(R.string.tap_on_map))
            }
        }
    }
}

@Composable
private fun BuyerFullScreenMapDialog(
    zoneCenter: LatLng,
    zoneRadiusKm: Double,
    sellerZoneAddress: String,
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
                    sellerZoneAddress = sellerZoneAddress,
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
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_desc))
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
                Icon(Icons.Default.MyLocation, contentDescription = stringResource(R.string.current_location_desc))
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.choose_your_address),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            stringResource(R.string.seller_address_value, sellerZoneAddress.ifBlank { stringResource(R.string.no_address) }),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.done))
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
    sellerZoneAddress: String,
    buyerLatLng: LatLng?
){
    val context = LocalContext.current
    Marker(
        state = MarkerState(position = zoneCenter),
        title = context.getString(R.string.seller_zone_marker),
        snippet = sellerZoneAddress.ifBlank { context.getString(R.string.seller_address_missing) }
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
            title = context.getString(R.string.your_address)
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

@Composable
private fun mapDeliveryError(error: String): String {
    return when {
        error.contains("Delivery is not available for this address", ignoreCase = true) ->
            stringResource(R.string.delivery_unavailable_address)
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
