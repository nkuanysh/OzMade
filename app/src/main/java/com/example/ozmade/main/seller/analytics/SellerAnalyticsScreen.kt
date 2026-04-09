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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerAnalyticsScreen(
    onBack: () -> Unit
) {
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Главная карточка дохода
            RevenueCard()

            Text(
                "ОСНОВНЫЕ ПОКАЗАТЕЛИ",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatSmallCard(
                    title = "Заказы",
                    value = "12",
                    icon = Icons.Default.ShoppingBag,
                    color = Color(0xFF64B5F6),
                    modifier = Modifier.weight(1f)
                )
                StatSmallCard(
                    title = "Просмотры",
                    value = "1.2k",
                    icon = Icons.Default.Visibility,
                    color = Color(0xFFFFB74D),
                    modifier = Modifier.weight(1f)
                )
            }

            // Популярные товары (заглушка)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Популярные товары", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    repeat(3) {
                        Row(
                            Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(40.dp).background(Color(0xFFF1F1F1), RoundedCornerShape(8.dp)))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Товар #${it + 1}", fontWeight = FontWeight.Medium)
                                Text("Продано: ${10 - it}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text("${(it + 1) * 5000} ₸", fontWeight = FontWeight.Bold)
                        }
                        if (it < 2) HorizontalDivider(color = Color(0xFFF1F1F1))
                    }
                }
            }
        }
    }
}

@Composable
private fun RevenueCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                )
            )
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("Ваш доход за месяц", color = Color.White.copy(0.8f), fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text("145,000 ₸", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(20.dp))
                Surface(
                    color = Color.White.copy(0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("+15% к прошлому месяцу", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
