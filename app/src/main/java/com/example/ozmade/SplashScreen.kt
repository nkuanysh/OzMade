package com.example.ozmade

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val transitionState = remember { MutableTransitionState(false).apply { targetState = true } }
    val transition = rememberTransition(transitionState, label = "SplashTransition")

    val scale by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow) },
        label = "Scale"
    ) { state -> if (state) 1f else 0.5f }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 1000) },
        label = "Alpha"
    ) { state -> if (state) 1f else 0f }

    val textAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 1000, delayMillis = 500) },
        label = "TextAlpha"
    ) { state -> if (state) 1f else 0f }

    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFBFA),
                        Color(0xFFFFF3E0),
                        Color(0xFFFFE0B2)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_removebg_preview),
                contentDescription = "OzMade Logo",
                modifier = Modifier
                    .size(220.dp)
                    .scale(scale)
                    .alpha(alpha)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "OzMade",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF6F00),
                modifier = Modifier.alpha(textAlpha)
            )
            
            Text(
                text = "Сделано с любовью",
                fontSize = 16.sp,
                color = Color.Gray.copy(alpha = 0.8f),
                modifier = Modifier.alpha(textAlpha)
            )
        }
        
        // Маленький индикатор загрузки снизу
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .alpha(textAlpha)
        ) {
            LoadingDots()
        }
    }
}

@Composable
fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFFFF6F00), androidx.compose.foundation.shape.CircleShape)
                    .alpha(if (index == 1) dotAlpha else 0.6f)
            )
        }
    }
}

@Composable
fun RegistrationScreenPlaceholder(onNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.image_removebg_preview),
            contentDescription = "OzMade Logo",
            modifier = Modifier.size(180.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onTimeout = {})
}
