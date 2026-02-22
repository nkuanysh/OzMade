package com.example.ozmade.main.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically

        ) {
            LangChip("Қаз", lang == AppLang.KAZ) { lang = AppLang.KAZ }
            Spacer(Modifier.width(8.dp))
            LangChip("Рус", lang == AppLang.RUS) { lang = AppLang.RUS }
        }

        Spacer(Modifier.height(10.dp))

        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                // Верх профиля в состоянии загрузки
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            is ProfileUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is ProfileUiState.Data -> {
                val user = state.user

                // ✅ Аватар по центру (пока без картинки: если url есть — позже подключим)
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    // Пока иконка. Когда будете готовы — я покажу Coil AsyncImage по user.avatarUrl
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // ✅ Имя + стрелка редактирования (как ты хотел)
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(onClick = onEditProfile)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name.ifBlank { "Без имени" },
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Редактировать профиль"
                    )
                }

                // ✅ Номер ниже
                Text(
                    text = user.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(20.dp))
            }
        }

        // Разделы (как у тебя)
        ProfileSectionButton(
            icon = Icons.Default.Notifications,
            title = "Уведомления",
            onClick = onNotifications
        )
        ProfileSectionButton(
            icon = Icons.Default.ShoppingCart,
            title = "История заказов",
            onClick = onOrderHistory
        )
        ProfileSectionButton(
            icon = Icons.Default.MailOutline,
            title = "Служба поддержки",
            onClick = onSupport
        )
        ProfileSectionButton(
            icon = Icons.Default.Info,
            title = "О приложении",
            onClick = onAbout
        )
        ProfileSectionButton(
            icon = Icons.Default.CheckCircle,
            title = "Стать продавцом",
            onClick = onBecomeSeller
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Выйти")
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileSectionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null)
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
        }
    }
}
@Composable
private fun LangChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = AssistChipDefaults.assistChipColors(
        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        labelColor = MaterialTheme.colorScheme.onSurface
    )
    AssistChip(onClick = onClick, label = { Text(text) }, colors = colors)
}
