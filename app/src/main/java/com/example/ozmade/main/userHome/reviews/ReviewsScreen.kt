package com.example.ozmade.main.userHome.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    uiState: ReviewsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when (uiState) {
                        is ReviewsUiState.Data -> "Отзывы (${uiState.titleCount})"
                        else -> "Отзывы"
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        when (uiState) {
            is ReviewsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(strokeWidth = 3.dp) }
            }

            is ReviewsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(24.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Повторить")
                    }
                }
            }

            is ReviewsUiState.Data -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    item {
                        RatingHeader(
                            avg = uiState.summary.averageRating,
                            ratingsCount = uiState.summary.ratingsCount
                        )
                    }

                    item {
                        HorizontalDivider(
                            thickness = 8.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    }

                    items(uiState.reviews, key = { it.id }) { r ->
                        ReviewCard(r)
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingHeader(avg: Double, ratingsCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.1f", avg),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                StarsRow(rating = avg, size = 18.dp)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$ratingsCount оценок",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(32.dp))

            // Rating distribution bars (dummy data for visual)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RatingBar(5, 0.8f)
                RatingBar(4, 0.15f)
                RatingBar(3, 0.03f)
                RatingBar(2, 0.01f)
                RatingBar(1, 0.01f)
            }
        }
    }
}

@Composable
private fun RatingBar(stars: Int, progress: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stars.toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(12.dp)
        )
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape),
            color = Color(0xFFFFC107),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun ReviewCard(r: ReviewUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Photo
            if (r.photoUrl != null) {
                AsyncImage(
                    model = r.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = r.userName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = r.userName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = r.dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            StarsRow(rating = r.rating, size = 14.dp)
        }
        
        Spacer(Modifier.height(12.dp))
        
        Text(
            text = r.text,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StarsRow(rating: Double, size: Dp) {
    val full = floor(rating).toInt().coerceIn(0, 5)
    val hasHalf = (rating - full) >= 0.5 && full < 5
    val empty = 5 - full - (if (hasHalf) 1 else 0)

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(full) { Star(filledFraction = 1f, size = size) }
        if (hasHalf) Star(filledFraction = 0.5f, size = size)
        repeat(empty) { Star(filledFraction = 0f, size = size) }
    }
}

@Composable
private fun Star(
    filledFraction: Float,
    size: Dp
) {
    val fill = Color(0xFFFFC107)
    val outline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)

    val path = remember { Path() }

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .size(size)
            .padding(end = 2.dp)
    ) {
        path.reset()
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f
        val outer = min(w, h) * 0.5f
        val inner = outer * 0.45f
        val startAngle = -Math.PI / 2.0
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) outer else inner
            val a = startAngle + i * (Math.PI / 5.0)
            val x = cx + (r * cos(a)).toFloat()
            val y = cy + (r * sin(a)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        drawPath(path = path, color = outline, style = Stroke(width = 1.dp.toPx()))

        if (filledFraction > 0f) {
            clipRect(left = 0f, top = 0f, right = w * filledFraction, bottom = h) {
                drawPath(path = path, color = fill, style = Fill)
            }
        }
    }
}
