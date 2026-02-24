package com.example.ozmade.main.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.seller.profile.SellerProfileViewModel
import com.example.ozmade.main.seller.profile.data.SellerProfileUiState

private enum class AppLang { KAZ, RUS }

@Composable
fun SellerProfileScreen(
    onBecomeBuyer: () -> Unit,
    onNotifications: () -> Unit = {},
    onArchive: () -> Unit = {},
    onQuality: () -> Unit = {},
    onDelivery: () -> Unit = {},
    onSupport: () -> Unit = {},
    onAbout: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: SellerProfileViewModel = hiltViewModel()
) {
    var lang by rememberSaveable { mutableStateOf(AppLang.RUS) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)          // ✅ скролл
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)             // ✅ чтобы кнопка "Выйти" не прилипала к краю
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
            is SellerProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(20.dp))
            }

            is SellerProfileUiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.load() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Повторить")
                }
                Spacer(Modifier.height(20.dp))
            }

            is SellerProfileUiState.Data -> {
                val p = state.profile

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(44.dp))
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = p.name.ifBlank { "Без имени" },
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "Статус: ${p.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Товаров: ${p.totalProducts}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(20.dp))
            }
        }

        // пункты меню
        ProfileSectionButton(Icons.Default.Notifications, "Уведомления", onNotifications)
        ProfileSectionButton(Icons.Default.DateRange, "Архив", onArchive)
        ProfileSectionButton(Icons.Default.Star, "Качество работы", onQuality)
        ProfileSectionButton(Icons.Default.Place, "Доставка", onDelivery)
        ProfileSectionButton(Icons.Default.MailOutline, "Служба поддержки", onSupport)
        ProfileSectionButton(Icons.Default.Info, "О приложении", onAbout)
        ProfileSectionButton(Icons.Default.Person, "Стать покупателем", onBecomeBuyer)

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = onLogout,
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
            ) { Icon(icon, contentDescription = null) }

            Spacer(Modifier.width(12.dp))

            Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
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