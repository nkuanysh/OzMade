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
    val phoneNumber = "+77077077072"
    val faqSections = remember { buildFaq() }
    val orangeAccent = Color(0xFFFF9800)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Помощь", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFFBFBFB)
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = orangeAccent.copy(0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.HeadsetMic,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = orangeAccent
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Как мы можем помочь?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(Modifier.height(4.dp))
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
                        containerColor = orangeAccent,
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
                        contentColor = orangeAccent,
                        onClick = onOpenSupportChat
                    )
                }
            }

            item {
                Text(
                    text = "Частые вопросы",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A),
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
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        onClick = onClick,
        shadowElevation = 2.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = if (containerColor == Color.White) Color(0xFFFFF3E0) else Color.White.copy(0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(title, fontWeight = FontWeight.ExtraBold, color = contentColor, fontSize = 17.sp)
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
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                section.items.forEachIndexed { index, item ->
                    FaqQuestionRow(item = item)
                    if (index != section.items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            thickness = 1.dp,
                            color = Color(0xFFF5F5F5)
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
            .padding(20.dp)
            .animateContentSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
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
            Spacer(Modifier.height(14.dp))
            Text(
                text = item.answer,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                lineHeight = 22.sp
            )
        }
    }
}

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
