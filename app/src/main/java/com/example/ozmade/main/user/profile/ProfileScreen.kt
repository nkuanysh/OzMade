package com.example.ozmade.main.user.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.user.profile.data.ProfileUiState
import com.example.ozmade.main.user.profile.data.ProfileViewModel

private enum class AppLang { KAZ, RUS }

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onEditProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onOrderHistory: () -> Unit = {},
    onSupport: () -> Unit = {},
    onAbout: () -> Unit = {},
    onBecomeSeller: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var lang by rememberSaveable { mutableStateOf(AppLang.RUS) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // --- ВЕРХНЯЯ ПАНЕЛЬ (Язык) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                LanguageSelector(selectedLang = lang, onLangSelected = { lang = it })
            }

            // --- БЛОК ПОЛЬЗОВАТЕЛЯ ---
            when (val state = uiState) {
                is ProfileUiState.Loading -> ProfileHeaderLoading()
                is ProfileUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is ProfileUiState.Data -> {
                    ProfileHeader(
                        name = state.user.name,
                        phone = state.user.phone,
                        onEdit = onEditProfile
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- МЕНЮ РАЗДЕЛОВ ---
            Text(
                text = "Личное",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            ProfileSectionCard {
                ProfileMenuItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Уведомления",
                    iconColor = Color(0xFF5C6BC0),
                    onClick = onNotifications
                )
                MenuDivider()
                ProfileMenuItem(
                    icon = Icons.Outlined.History,
                    title = "История заказов",
                    iconColor = Color(0xFF66BB6A),
                    onClick = onOrderHistory
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Поддержка и инфо",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            ProfileSectionCard {
                ProfileMenuItem(
                    icon = Icons.Outlined.SupportAgent,
                    title = "Служба поддержки",
                    iconColor = Color(0xFF26A69A),
                    onClick = onSupport
                )
                MenuDivider()
                ProfileMenuItem(
                    icon = Icons.Outlined.Info,
                    title = "О приложении",
                    iconColor = Color(0xFF78909C),
                    onClick = onAbout
                )
            }

            Spacer(Modifier.height(24.dp))

            // --- СПЕЦИАЛЬНАЯ КНОПКА (СТАТЬ ПРОДАВЦОМ) ---
            BecomeSellerCard(onClick = onBecomeSeller)

            Spacer(Modifier.weight(1f))

            // --- КНОПКА ВЫХОДА ---
            TextButton(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE53935))
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Выйти из аккаунта", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileHeader(name: String, phone: String, onEdit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // Кнопка быстрого редактирования прямо на фото
            Surface(
                modifier = Modifier.size(32.dp).offset(x = (-4).dp, y = (-4).dp)
                    .clickable { onEdit() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 4.dp
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.padding(8.dp), tint = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = name.ifBlank { "Ваше имя" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = phone,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
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
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
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
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
private fun BecomeSellerCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)) // Сначала обрезаем форму
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6A1B9A), Color(0xFF8E24AA))
                )
            )
            .clickable { onClick() }, // Обработка нажатия
        color = Color.Transparent, // Делаем саму поверхность прозрачной, чтобы был виден фон
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Стать продавцом",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "Откройте свой магазин",
                    color = Color.White.copy(0.8f),
                    fontSize = 13.sp
                )
            }
            Icon(
                Icons.Default.Storefront,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun LanguageSelector(selectedLang: AppLang, onLangSelected: (AppLang) -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier.height(36.dp).width(110.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row {
            AppLang.values().forEach { lang ->
                val isSelected = selectedLang == lang
                val bgColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                val contentColor by animateColorAsState(if (isSelected) Color.White else Color.Gray)

                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .clickable { onLangSelected(lang) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lang == AppLang.KAZ) "ҚАЗ" else "РУС",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color(0xFFF1F1F1))
}

@Composable
private fun ProfileHeaderLoading() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray.copy(0.3f)))
        Spacer(Modifier.height(16.dp))
        Box(Modifier.width(150.dp).height(24.dp).background(Color.LightGray.copy(0.3f)))
    }
}