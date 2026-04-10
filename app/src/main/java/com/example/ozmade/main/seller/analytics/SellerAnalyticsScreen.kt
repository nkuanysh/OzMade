package com.example.ozmade.main.seller.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ozmade.utils.ImageUtils
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerAnalyticsScreen(
    onBack: () -> Unit,
    viewModel: SellerAnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Аналитика продаж", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF9800))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Главная карточка дохода
                RevenueCard(uiState.totalRevenue, uiState.revenueGrowth)

                Text(
                    "ОСНОВНЫЕ ПОКАЗАТЕЛИ",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatSmallCard(
                        title = "Заказы",
                        value = uiState.ordersCount.toString(),
                        icon = Icons.Default.ShoppingBag,
                        color = Color(0xFF64B5F6),
                        modifier = Modifier.weight(1f)
                    )
                    StatSmallCard(
                        title = "Просмотры",
                        value = formatViews(uiState.viewsCount),
                        icon = Icons.Default.Visibility,
                        color = Color(0xFFFFB74D),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Популярные товары
                if (uiState.popularProducts.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Популярные товары", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(Modifier.height(16.dp))
                            uiState.popularProducts.forEachIndexed { index, product ->
                                Row(
                                    Modifier.padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = ImageUtils.formatImageUrl(product.imageUrl),
                                        contentDescription = null,
                                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text("Продано: ${product.salesCount}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Text(formatPrice(product.revenue), fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF9800))
                                }
                                if (index < uiState.popularProducts.size - 1) HorizontalDivider(color = Color(0xFFF1F1F1).copy(0.5f))
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun RevenueCard(revenue: Double, growth: Double) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                )
            )
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("Общий доход", color = Color.White.copy(0.8f), fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text(formatPrice(revenue), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(20.dp))
                Surface(
                    color = Color.White.copy(0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Статистика по заказам", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatSmallCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = color.copy(alpha = 0.1f)
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "KZ"))
    formatter.maximumFractionDigits = 0
    return formatter.format(price).replace("KZT", "₸").replace("₸", " ₸")
}

private fun formatViews(views: Int): String {
    return if (views >= 1000) {
        String.format(Locale.US, "%.1fk", views / 1000f)
    } else {
        views.toString()
    }
}
