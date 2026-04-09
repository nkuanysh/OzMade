package com.example.ozmade.main.seller.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ozmade.main.orders.data.DeliveryType
import com.example.ozmade.main.orders.data.OrderStatus
import com.example.ozmade.main.orders.data.deliveryTitle
import com.example.ozmade.main.orders.data.statusTitle
import com.example.ozmade.main.seller.orders.data.SellerOrdersViewModel
import com.example.ozmade.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrderDetailsRoute(
    orderId: Int,
    onBack: () -> Unit,
    viewModel: SellerOrdersViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val order = remember(ui, orderId) { viewModel.findById(orderId) }

    var code by remember { mutableStateOf("") }
    var shipComment by remember { mutableStateOf("") }

    val orangeColor = Color(0xFFFF9800)

    LaunchedEffect(Unit) { if (order == null) viewModel.load() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Заказ #$orderId", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (order == null) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = orangeColor)
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
                    StatusIcon(order.status)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = statusTitle(order.status),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Обновлено: ${order.createdAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // ---- Product Details ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(80.dp),
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
                        Column {
                            Text(
                                order.productTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Кол-во: ${order.quantity} шт.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = "${order.totalCost} ₸",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            // ---- Delivery Info ----
            InfoSection(
                title = "Доставка",
                icon = Icons.Default.LocalShipping
            ) {
                InfoRow("Способ", deliveryTitle(order.deliveryType))
                
                when (order.deliveryType) {
                    DeliveryType.PICKUP -> {
                        order.pickupAddress?.let { InfoRow("Адрес самовывоза", it) }
                        order.pickupTime?.let { InfoRow("Время работы", it) }
                    }
                    DeliveryType.MY_DELIVERY -> {
                        order.shippingAddressText?.let { InfoRow("Адрес доставки", it) }
                    }
                    DeliveryType.INTERCITY -> {
                        order.shippingAddressText?.let { InfoRow("Адрес (межгород)", it) }
                    }
                }
            }

            // ---- Actions ----
            ActionSection(
                orderStatus = order.status,
                deliveryType = order.deliveryType,
                confirmCode = order.confirmCode,
                shipComment = shipComment,
                code = code,
                onShipCommentChange = { shipComment = it },
                onCodeChange = { code = it },
                onCancel = { viewModel.cancel(order.id) },
                onConfirm = { viewModel.confirm(order.id) },
                onShipped = { viewModel.shipped(order.id, shipComment.ifBlank { null }) },
                onComplete = { viewModel.complete(order.id, code) },
                orangeColor = orangeColor
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatusIcon(status: String) {
    val (color, icon) = when (status) {
        OrderStatus.PENDING_SELLER -> Color(0xFFFFA000) to Icons.Default.NotificationsActive
        OrderStatus.CONFIRMED -> Color(0xFF1E88E5) to Icons.Default.Autorenew
        OrderStatus.READY_OR_SHIPPED -> Color(0xFF43A047) to Icons.Default.LocalShipping
        OrderStatus.COMPLETED -> Color(0xFF757575) to Icons.Default.CheckCircle
        else -> Color(0xFFE53935) to Icons.Default.Cancel
    }

    Surface(
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.1f)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun InfoSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
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
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, textAlign = TextAlign.End, modifier = Modifier.weight(1f).padding(start = 16.dp))
    }
}

@Composable
private fun ActionSection(
    orderStatus: String,
    deliveryType: String,
    confirmCode: String?,
    shipComment: String,
    code: String,
    onShipCommentChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    onShipped: () -> Unit,
    onComplete: () -> Unit,
    orangeColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        when (orderStatus) {
            OrderStatus.PENDING_SELLER -> {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Отклонить", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
                    ) {
                        Text("Подтвердить", fontWeight = FontWeight.Bold)
                    }
                }
            }

            OrderStatus.CONFIRMED -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        if (deliveryType == DeliveryType.INTERCITY) {
                            Text("Товар готов? Отправьте его и укажите трек-номер.", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = shipComment,
                                onValueChange = onShipCommentChange,
                                label = { Text("Комментарий или трек-номер") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = onShipped,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
                            ) { Text("Я отправил товар", fontWeight = FontWeight.Bold) }
                        } else {
                            Text("Выдайте товар покупателю и попросите его назвать 4-значный код подтверждения.", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = code,
                                onValueChange = { onCodeChange(it.filter { ch -> ch.isDigit() }.take(4)) },
                                label = { Text("Код (4 цифры)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 24.sp, fontWeight = FontWeight.Bold),
                                singleLine = true
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = onComplete,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                                enabled = code.length == 4
                            ) { Text("Подтвердить выдачу", fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }

            OrderStatus.READY_OR_SHIPPED -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFF2E7D32))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Ожидаем подтверждения получения от покупателя.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
