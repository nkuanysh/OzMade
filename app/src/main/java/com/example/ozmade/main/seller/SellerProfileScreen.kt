package com.example.ozmade.main.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SellerProfileScreen(
    onBecomeBuyer: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF8F9FA) // Мягкий фон для контраста с белыми карточками
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // --- ЗАГОЛОВОК И МАГАЗИН ---
            Text(
                text = "Мой магазин",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            // --- КАРТОЧКА СТАТИСТИКИ ---
            SellerStatsRow()

            Spacer(Modifier.height(24.dp))

            // --- МЕНЮ УПРАВЛЕНИЯ ---
            Text(
                text = "Управление",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 0.5.dp
            ) {
                Column {
                    SellerMenuItem(Icons.Outlined.Settings, "Настройки магазина", "Название, описание, логотип") { }
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color(0xFFF1F1F1))
                    SellerMenuItem(Icons.Outlined.Payments, "Реквизиты и оплата", "Куда приходят деньги") { }
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color(0xFFF1F1F1))
                    SellerMenuItem(Icons.Outlined.Analytics, "Аналитика продаж", "Статистика за месяц") { }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- ПЕРЕКЛЮЧАТЕЛЬ В РЕЖИМ ПОКУПАТЕЛЯ ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                onClick = onBecomeBuyer
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.SwitchAccount,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Вернуться в покупки",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            "Переключиться на профиль покупателя",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.7f)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Кнопка выхода (опционально для продавца)
            TextButton(
                onClick = { /* Logout logic */ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Выйти из системы", color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SellerStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Товары", "24", Icons.Default.Inventory, Modifier.weight(1f))
        StatCard("Рейтинг", "4.9", Icons.Default.Star, Modifier.weight(1f), Color(0xFFFFA000))
        StatCard("Заказы", "12", Icons.Default.LocalShipping, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun SellerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}