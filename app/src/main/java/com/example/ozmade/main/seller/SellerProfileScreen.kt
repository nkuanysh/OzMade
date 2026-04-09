package com.example.ozmade.main.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.seller.profile.SellerProfileViewModel
import com.example.ozmade.main.seller.profile.data.SellerProfileUi
import com.example.ozmade.main.seller.profile.data.SellerProfileUiState
import java.util.Locale

@Composable
fun SellerProfileScreen(
    onOpenProducts: () -> Unit = {},
    onOpenQuality: () -> Unit = {},
    onOpenOrders: () -> Unit = {},
    onOpenStoreSettings: () -> Unit = {},
    onOpenDelivery: () -> Unit = {},
    onOpenPayments: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onBecomeBuyer: () -> Unit,
    viewModel: SellerProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        when (val state = uiState) {
            is SellerProfileUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF9800))
                }
            }
            is SellerProfileUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(20.dp)) {
                        Icon(Icons.Default.ErrorOutline, null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                        Spacer(Modifier.height(16.dp))
                        Text("Ошибка: ${state.message}", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.load() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                        ) {
                            Text("Повторить")
                        }
                    }
                }
            }
            is SellerProfileUiState.Data -> {
                SellerProfileContent(
                    profile = state.profile,
                    padding = padding,
                    onOpenProducts = onOpenProducts,
                    onOpenQuality = onOpenQuality,
                    onOpenOrders = onOpenOrders,
                    onOpenStoreSettings = onOpenStoreSettings,
                    onOpenDelivery = onOpenDelivery,
                    onOpenPayments = onOpenPayments,
                    onOpenAnalytics = onOpenAnalytics,
                    onBecomeBuyer = onBecomeBuyer
                )
            }
        }
    }
}

@Composable
private fun SellerProfileContent(
    profile: SellerProfileUi,
    padding: PaddingValues,
    onOpenProducts: () -> Unit,
    onOpenQuality: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenStoreSettings: () -> Unit,
    onOpenDelivery: () -> Unit,
    onOpenPayments: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onBecomeBuyer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp)
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = profile.name.ifBlank { "Мой магазин" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "МОЯ СТАТИСТИКА",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        )

        SellerStatsRow(
            profile = profile,
            onOpenQuality = onOpenQuality
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "УПРАВЛЕНИЕ",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        ProfileSectionCard {
            SellerMenuItem(
                icon = Icons.Outlined.Settings,
                title = "Настройки магазина",
                subtitle = "Название, описание, логотип",
                iconColor = Color(0xFF5C6BC0),
                onClick = onOpenStoreSettings
            )
            MenuDivider()
            SellerMenuItem(
                icon = Icons.Outlined.LocalShipping,
                title = "Доставка",
                subtitle = "Самовывоз, курьер, почта",
                iconColor = Color(0xFF66BB6A),
                onClick = onOpenDelivery
            )
            MenuDivider()
            SellerMenuItem(
                icon = Icons.Outlined.Payments,
                title = "Реквизиты и оплата",
                subtitle = "Куда приходят деньги",
                iconColor = Color(0xFFFFA726),
                onClick = onOpenPayments
            )
            MenuDivider()
            SellerMenuItem(
                icon = Icons.Outlined.Analytics,
                title = "Аналитика продаж",
                subtitle = "Статистика за месяц",
                iconColor = Color(0xFF26A69A),
                onClick = onOpenAnalytics
            )
        }

        Spacer(Modifier.height(24.dp))

        ReturnToBuyerCard(onClick = onBecomeBuyer)

        Spacer(Modifier.height(32.dp))

        TextButton(
            onClick = { /* Logout logic */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE53935))
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Выйти из системы", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SellerStatsRow(
    profile: SellerProfileUi,
    onOpenQuality: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "Рейтинг",
            value = if (profile.rating > 0) String.format(Locale.US, "%.1f", profile.rating) else "—",
            icon = Icons.Default.Star,
            modifier = Modifier.fillMaxWidth().clickable { onOpenQuality() },
            iconColor = Color(0xFFFFA000)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ProfileSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun SellerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = iconColor.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = iconColor
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ReturnToBuyerCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF455A64), Color(0xFF263238))
                )
            )
            .clickable { onClick() },
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Вернуться в покупки",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "Перейти в профиль покупателя",
                    color = Color.White.copy(0.8f),
                    fontSize = 13.sp
                )
            }
            Icon(
                Icons.Default.SwitchAccount,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = Color(0xFFF1F1F1)
    )
}
