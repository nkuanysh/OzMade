package com.example.ozmade.main.user.orders

import com.example.ozmade.R
import androidx.compose.ui.res.stringResource
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.ozmade.main.delivery.formatDeliveryDateRange
import com.example.ozmade.main.delivery.formatDeliveryPrice
import com.example.ozmade.main.orders.data.DeliveryType
import com.example.ozmade.main.orders.data.OrderStatus
import com.example.ozmade.main.orders.data.deliveryTitle
import com.example.ozmade.main.orders.data.statusTitle
import com.example.ozmade.main.user.orders.data.BuyerOrdersViewModel
import com.example.ozmade.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerOrderDetailsRoute(
    orderId: Int,
    onBack: () -> Unit,
    onChat: (sellerId: Int, sellerName: String, productId: Int, productTitle: String, price: Int) -> Unit = { _, _, _, _, _ -> },
    onOpenProduct: (Int) -> Unit = {},
    viewModel: BuyerOrdersViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val order = remember(ui, orderId) { viewModel.findById(orderId) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.load() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.load()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.order_number, orderId), fontWeight = FontWeight.Bold) },
                navigationIcon = { 
                    IconButton(onClick = onBack) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc)) 
                    } 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (order == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---- Status Card ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusIndicator(order.status)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = statusTitle(order.status),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.order_date, order.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // ---- Product Info ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(90.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0F0F0)
                        ) {
                            AsyncImage(
                                model = ImageUtils.formatImageUrl(order.productImageUrl),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                order.productTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                            Text(
                                stringResource(R.string.seller_label_value, order.sellerName ?: stringResource(R.string.master_default)),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    stringResource(R.string.pieces_count, order.quantity),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = "${order.totalCost} ₸",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // ---- Delivery Info ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.delivery_info), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                    Spacer(Modifier.height(12.dp))
                    
                    InfoRow(stringResource(R.string.delivery_method), deliveryTitle(order.deliveryType))
                    
                    when (order.deliveryType) {
                        DeliveryType.PICKUP -> {
                            InfoRow(stringResource(R.string.address), order.pickupAddress ?: stringResource(R.string.dash))
                            InfoRow(stringResource(R.string.working_hours), order.pickupTime ?: stringResource(R.string.dash))
                        }
                        DeliveryType.MY_DELIVERY -> {
                            InfoRow(stringResource(R.string.shipping_address), order.shippingAddressText ?: stringResource(R.string.dash))
                        }
                        DeliveryType.INTERCITY -> {
                            val intercity = order.intercityDelivery
                            if (intercity != null) {
                                InfoRow("Служба", intercity.provider)
                                InfoRow("Маршрут", stringResource(R.string.intercity_route, intercity.fromCity, intercity.toCity))
                                InfoRow(
                                    stringResource(R.string.intercity_address),
                                    intercity.toAddress.ifBlank { order.shippingAddressText ?: stringResource(R.string.dash) }
                                )
                                InfoRow("Примерная стоимость", formatDeliveryPrice(intercity.price, intercity.currency))
                                InfoRow("Срок", "${intercity.minDays}–${intercity.maxDays} дня")
                                if (intercity.estimatedDateFrom.isNotBlank() && intercity.estimatedDateTo.isNotBlank()) {
                                    InfoRow(
                                        "Ожидаемая дата",
                                        formatDeliveryDateRange(intercity.estimatedDateFrom, intercity.estimatedDateTo)
                                    )
                                }
                            } else {
                                InfoRow(stringResource(R.string.intercity_address), order.shippingAddressText ?: stringResource(R.string.dash))
                            }
                        }
                    }
                }
            }

            // ---- Confirmation Code ----
            if (!order.confirmCode.isNullOrBlank() && 
                (order.status == OrderStatus.CONFIRMED || order.status == OrderStatus.READY_OR_SHIPPED)) {
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.confirmation_code_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            order.confirmCode,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 8.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.show_code_to_seller),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ---- Actions ----
            when (order.status) {
                OrderStatus.PENDING_SELLER -> {
                    Button(
                        onClick = { viewModel.cancel(order.id) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                    ) { Text(stringResource(R.string.cancel_order), fontWeight = FontWeight.Bold) }
                }

                OrderStatus.CONFIRMED, OrderStatus.READY_OR_SHIPPED -> {
                    if (order.status == OrderStatus.READY_OR_SHIPPED && order.deliveryType == DeliveryType.INTERCITY) {
                        Button(
                            onClick = { viewModel.received(order.id) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text(stringResource(R.string.received_product), fontWeight = FontWeight.Bold) }
                        
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE3F2FD)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    stringResource(R.string.receive_warning),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1976D2)
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { 
                            onChat(
                                order.sellerId ?: 0,
                                order.sellerName ?: context.getString(R.string.seller_default),
                                order.productId,
                                order.productTitle,
                                order.price.toInt()
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text(stringResource(R.string.write_seller), fontWeight = FontWeight.Bold) }
                }

                OrderStatus.COMPLETED -> {
                    if (!order.isReviewed) {
                        var showDialog by remember { mutableStateOf(false) }
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(stringResource(R.string.leave_review), fontWeight = FontWeight.Bold)
                        }

                        if (showDialog) {
                            ReviewDialog(
                                onDismiss = { showDialog = false },
                                onSubmit = { rating, text ->
                                    viewModel.postReview(order.id, order.productId, rating, text) {
                                        showDialog = false
                                        viewModel.load() // Перезагружаем заказы, чтобы обновить флаг isReviewed
                                    }
                                }
                            )
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                stringResource(R.string.already_reviewed),
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                else -> {
                    // Show nothing or default button for Cancelled/Expired
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatusIndicator(status: String) {
    val color = when (status) {
        OrderStatus.PENDING_SELLER -> Color(0xFFFFA000)
        OrderStatus.CONFIRMED -> Color(0xFF1E88E5)
        OrderStatus.READY_OR_SHIPPED -> Color(0xFF43A047)
        OrderStatus.COMPLETED -> Color(0xFF4CAF50)
        else -> Color(0xFFE53935)
    }

    Surface(
        modifier = Modifier.size(12.dp),
        shape = CircleShape,
        color = color
    ) {}
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(
            value, 
            style = MaterialTheme.typography.bodyMedium, 
            fontWeight = FontWeight.Medium, 
            textAlign = TextAlign.End, 
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}

@Composable
fun ReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Double, String) -> Unit
) {
    var rating by remember { mutableDoubleStateOf(5.0) }
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.review_yours_title)) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val starIndex = (index + 1).toDouble()
                        IconButton(onClick = { rating = starIndex }) {
                            Icon(
                                imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (starIndex <= rating) Color(0xFFFFB400) else Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text(stringResource(R.string.review_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, text) },
                enabled = text.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.send))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
