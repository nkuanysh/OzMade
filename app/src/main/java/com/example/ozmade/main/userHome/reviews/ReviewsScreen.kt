package com.example.ozmade.main.userHome.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            CenterAlignedTopAppBar(
                title = {
                    val title = when (uiState) {
                        is ReviewsUiState.Data -> "Отзывы ${uiState.titleCount}"
                        else -> "Отзывы"
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        when (uiState) {
            is ReviewsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is ReviewsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                        Text("Повторить")
                    }
                }
            }

            is ReviewsUiState.Data -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        RatingHeader(
                            avg = uiState.summary.averageRating,
                            ratingsCount = uiState.summary.ratingsCount
                        )
                    }

                    items(uiState.reviews, key = { it.id }) { r ->
                        ReviewCard(r)
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingHeader(avg: Double, ratingsCount: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format("%.1f", avg),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                StarsRow(rating = avg, size = 20.dp)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "$ratingsCount оценки",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(r: ReviewUi) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(r.userName, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    StarsRow(rating = r.rating, size = 16.dp)
                }
                Text(
                    text = r.dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(r.text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/**
 * 5 красивых жёлтых звёзд с половинкой (без зависимостей на extended icons).
 */
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
    val fill = Color(0xFFFFC107)          // красивый "gold"
    val outline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f)

    val path = remember { Path() }

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .size(size)
            .padding(end = 4.dp)
    ) {
        // строим путь звезды под текущий размер canvas
        path.reset()
        val w = this.size.width
        val h = this.size.height

        val cx = w / 2f
        val cy = h / 2f

        val outer = min(w, h) * 0.50f
        val inner = outer * 0.50f

        // 5 лучей = 10 точек (outer/inner)
        val startAngle = -Math.PI / 2.0
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) outer else inner
            val a = startAngle + i * (Math.PI / 5.0)
            val x = cx + (r * cos(a)).toFloat()
            val y = cy + (r * sin(a)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        // пустая звезда (контур)
        drawPath(
            path = path,
            color = outline,
            style = Stroke(width = 1.6f)
        )

        // заливка (полная или половина)
        if (filledFraction > 0f) {
            clipRect(
                left = 0f,
                top = 0f,
                right = w * filledFraction.coerceIn(0f, 1f),
                bottom = h
            ) {
                drawPath(
                    path = path,
                    color = fill,
                    style = Fill
                )
            }
        }
    }
}
