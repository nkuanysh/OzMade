package com.example.ozmade.main.user.profile.support

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onClose: () -> Unit,
    onOpenSupportChat: () -> Unit = {}
) {
    val context = LocalContext.current
    val phoneNumber = "+77077077070"
    val faqSections = remember { buildFaq() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Помощь", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA) // Мягкий фон
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Приветственный блок
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(0.4f)
                    ) {
                        Icon(
                            Icons.Outlined.HeadsetMic,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Как мы можем помочь?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Отвечаем ежедневно с 8:00 до 22:00",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            // Кнопки связи
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SupportActionCard(
                        title = "Позвонить",
                        subtitle = "Быстрый ответ",
                        icon = Icons.Outlined.PhoneInTalk,
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                            context.startActivity(intent)
                        }
                    )
                    SupportActionCard(
                        title = "Чат",
                        subtitle = "Написать нам",
                        icon = Icons.Outlined.ChatBubbleOutline,
                        modifier = Modifier.weight(1f),
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = onOpenSupportChat
                    )
                }
            }

            item {
                Text(
                    text = "Частые вопросы",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // FAQ
            items(faqSections) { section ->
                FaqSectionCard(section = section)
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun SupportActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        onClick = onClick,
        shadowElevation = if (containerColor == Color.White) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = contentColor)
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = contentColor, fontSize = 16.sp)
                Text(subtitle, color = contentColor.copy(0.7f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun FaqSectionCard(section: FaqSection) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = section.title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
            Column {
                section.items.forEachIndexed { index, item ->
                    FaqQuestionRow(item = item)
                    if (index != section.items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFF1F1F1)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqQuestionRow(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(16.dp)
            .animateContentSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.rotate(rotation)
            )
        }
        if (expanded) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = item.answer,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        }
    }
}

// Данные остаются те же (FaqSection, FaqItem, buildFaq)
private data class FaqSection(val title: String, val items: List<FaqItem>)
private data class FaqItem(val question: String, val answer: String)

private fun buildFaq(): List<FaqSection> {
    return listOf(
        FaqSection(
            title = "Заказы",
            items = listOf(
                FaqItem("Как оформить заказ?", "Нажмите «Заказать» или «Чат» на странице товара. Оплата происходит напрямую продавцу."),
                FaqItem("Что если продавец молчит?", "Попробуйте написать еще раз. Если ответа нет более 24 часов — выберите другой товар.")
            )
        ),
        FaqSection(
            title = "Безопасность",
            items = listOf(
                FaqItem("Как не попасться мошенникам?", "Не переводите деньги без подтверждения наличия товара и проверяйте рейтинг продавца.")
            )
        )
    )
}