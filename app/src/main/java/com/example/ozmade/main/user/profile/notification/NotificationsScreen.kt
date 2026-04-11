package com.example.ozmade.main.user.profile.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    var items by remember { mutableStateOf(NotificationStorage.getAll()) }

    DisposableEffect(Unit) {
        val listener = {
            items = NotificationStorage.getAll()
        }
        NotificationStorage.subscribe(listener)
        onDispose {
            NotificationStorage.unsubscribe(listener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Уведомления",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (items.isNotEmpty()) {
                        IconButton(onClick = { NotificationStorage.clear() }) {
                            Icon(Icons.Outlined.DeleteOutline, contentDescription = "Очистить всё")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (items.isEmpty()) {
            EmptyNotifications(padding)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    val sdf = remember { SimpleDateFormat("HH:mm, dd MMM", Locale("ru")) }
    val dateStr = remember(item.timestamp) { sdf.format(Date(item.timestamp)) }

    val iconColor = when (item.type) {
        "ORDER" -> Color(0xFF4CAF50) // Green for orders
        "CHAT" -> Color(0xFF2196F3) // Blue for chats
        else -> Color(0xFFFF9800) // Orange for others
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
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
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray
                    )
                )
            }
        }
    }
}

@Composable
fun EmptyNotifications(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.LightGray
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Уведомлений пока нет",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Здесь будут появляться новости и важные события",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
