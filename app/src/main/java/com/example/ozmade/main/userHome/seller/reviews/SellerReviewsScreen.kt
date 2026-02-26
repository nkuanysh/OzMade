package com.example.ozmade.main.userHome.seller.reviews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerReviewsScreen(
    uiState: SellerReviewsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenProduct: (String) -> Unit

) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val title = when (uiState) {
                        is SellerReviewsUiState.Data -> "Отзывы продавца ${uiState.header.reviewsCount}"
                        else -> "Отзывы продавца"
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
            is SellerReviewsUiState.Loading -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is SellerReviewsUiState.Error -> {
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

            is SellerReviewsUiState.Data -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SellerReviewsHeader(header = uiState.header)
                    }

                    items(uiState.reviews, key = { it.id }) { r ->
                        SellerReviewCard(
                            review = r,
                            onOpenProduct = onOpenProduct                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerReviewsHeader(header: SellerReviewsHeaderUi) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = header.sellerName,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(10.dp))

            // рейтинг слева, справа колонка: звёзды + "оценок" снизу
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%.1f", header.averageRating),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                )

                Spacer(Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    StarsRow(rating = header.averageRating, size = 20.dp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${header.ratingsCount} оценок",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SellerReviewCard(
    review: SellerReviewUi,
    onOpenProduct: (String) -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {

            // строка: дата слева, имя справа
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Text(
                    text = review.dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = review.userName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            // строка: товар (синий) + звёзды справа, если длинный - "..."
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = review.productTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenProduct(review.productId) }
                )
                StarsRow(rating = review.rating, size = 16.dp)
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * 5 красивых жёлтых звёзд с половинкой
 */
@Composable
private fun StarsRow(rating: Double, size: Dp) {
    val full = floor(rating).toInt().coerceIn(0, 5)
    val hasHalf = (rating - full) >= 0.5 && full < 5
    val empty = 5 - full - (if (hasHalf) 1 else 0)

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(full) { Star(filledFraction = 1f, starSize = size) }
        if (hasHalf) Star(filledFraction = 0.5f, starSize = size)
        repeat(empty) { Star(filledFraction = 0f, starSize = size) }
    }
}

@Composable
private fun Star(
    filledFraction: Float,
    starSize: Dp
) {
    val fill = Color(0xFFFFC107) // gold
    val outline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f)

    val path = remember { Path() }

    Canvas(
        modifier = Modifier
            .size(starSize)
            .padding(end = 4.dp)
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // строим путь звезды под размер canvas
        path.reset()
        val outer = min(w, h) * 0.50f
        val inner = outer * 0.50f

        val startAngle = -Math.PI / 2.0
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) outer else inner
            val a = startAngle + i * (Math.PI / 5.0)
            val x = cx + (r * cos(a)).toFloat()
            val y = cy + (r * sin(a)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        // контур пустой звезды
        drawPath(
            path = path,
            color = outline,
            style = Stroke(width = 1.6f)
        )

        // заливка по проценту (полная/половина)
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
