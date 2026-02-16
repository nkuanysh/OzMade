package com.example.ozmade.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onEditProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onOrderHistory: () -> Unit = {},
    onSupport: () -> Unit = {},
    onAbout: () -> Unit = {},
    onBecomeSeller: () -> Unit = {},
) {
    // Пока заглушки данных (потом возьмёте из Firebase/Firestore)
    val name = "Нурсултан"
    val phone = "+7 777 123 45 67"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(14.dp))

        // верхняя строка (можно оставить пустой, или потом добавить настройки/уведомления)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onNotifications) {
                Icon(Icons.Default.Notifications, contentDescription = "Уведомления")
            }
        }

        Spacer(Modifier.height(10.dp))

        // аватар
        Box(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        // имя + стрелка (редактировать)
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onEditProfile)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
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

        // номер
        Text(
            text = phone,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(20.dp))

        // Разделы
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

        // Выйти (внизу)
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
