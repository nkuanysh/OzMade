package com.example.ozmade.main.seller.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOnboardingScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val orangeColor = Color(0xFFFF9800)
    val lightOrange = Color(0xFFFFF3E0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // Иллюстрация / Иконка
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(lightOrange),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = orangeColor
                )
            }

            Spacer(Modifier.height(40.dp))

            Text(
                text = "Станьте продавцом\nв OzMade",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 34.sp
                ),
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Начните продавать товары собственного производства, домашнюю еду и уникальное ремесло напрямую вашим покупателям.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Gray,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Преимущества
            OnboardingFeatureItem(
                icon = Icons.Default.Done,
                text = "Прямая связь с покупателями",
                accentColor = orangeColor
            )
            OnboardingFeatureItem(
                icon = Icons.Default.Done,
                text = "Простое управление товарами",
                accentColor = orangeColor
            )
            OnboardingFeatureItem(
                icon = Icons.Default.Done,
                text = "Безопасные сделки",
                accentColor = orangeColor
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
            ) {
                Text(
                    "Начать регистрацию",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OnboardingFeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = accentColor
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color.DarkGray
        )
    }
}
