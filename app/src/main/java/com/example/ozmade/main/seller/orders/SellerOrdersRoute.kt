package com.example.ozmade.main.seller.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.ozmade.main.orders.data.OrderStatus
import com.example.ozmade.main.orders.data.OrderUi
import com.example.ozmade.main.orders.data.deliveryTitle
import com.example.ozmade.main.seller.orders.data.SellerOrdersUiState
import com.example.ozmade.main.seller.orders.data.SellerOrdersViewModel
import com.example.ozmade.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrdersRoute(
    onOpenOrder: (Int) -> Unit,
    viewModel: SellerOrdersViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Активные", "Завершенные", "Отмененные")
    val lifecycleOwner = LocalLifecycleOwner.current

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
            TopAppBar(
                title = { Text("Заказы", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFFFF9800),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFFFF9800)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = Color(0xFFFF9800),
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            when (ui) {
                is SellerOrdersUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF9800))
                }

                is SellerOrdersUiState.Error -> {
                    val msg = (ui as SellerOrdersUiState.Error).message
                    ErrorState(msg) { viewModel.load() }
                }

                is SellerOrdersUiState.Data -> {
                    val allOrders = (ui as SellerOrdersUiState.Data).orders
                    val filteredOrders = when (selectedTab) {
                        0 -> allOrders.filter {
                            it.status == OrderStatus.PENDING_SELLER ||
                                    it.status == OrderStatus.CONFIRMED ||
                                    it.status == OrderStatus.READY_OR_SHIPPED
                        }
                        1 -> allOrders.filter { it.status == OrderStatus.COMPLETED }
                        else -> allOrders.filter {
                            it.status == OrderStatus.CANCELLED_BY_BUYER ||
                                    it.status == OrderStatus.CANCELLED_BY_SELLER ||
                                    it.status == OrderStatus.EXPIRED
                        }
                    }

                    if (filteredOrders.isEmpty()) {
                        EmptyState(tabs[selectedTab])
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredOrders, key = { it.id }) { order ->
                                SellerOrderCard(order = order, onClick = { onOpenOrder(order.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerOrderCard(order: OrderUi, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Surface(
                modifier = Modifier.size(70.dp),
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

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Заказ #${order.id}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                    StatusBadge(order.status)
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = order.productTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = order.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = deliveryTitle(order.deliveryType),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5C6BC0),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${order.totalCost} ₸",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        OrderStatus.PENDING_SELLER -> Color(0xFFFFA000) to "Новый"
        OrderStatus.CONFIRMED -> Color(0xFF1E88E5) to "В работе"
        OrderStatus.READY_OR_SHIPPED -> Color(0xFF43A047) to "Отправлен"
        OrderStatus.COMPLETED -> Color(0xFF757575) to "Завершен"
        else -> Color(0xFFE53935) to "Отменен"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyState(tabName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = Color(0xFFF0F0F0)
        ) {
            Icon(
                Icons.Default.Inventory2,
                contentDescription = null,
                modifier = Modifier.padding(20.dp),
                tint = Color.LightGray
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Нет заказов",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "В разделе \"$tabName\" пока пусто",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
        ) {
            Text("Повторить")
        }
    }
}
