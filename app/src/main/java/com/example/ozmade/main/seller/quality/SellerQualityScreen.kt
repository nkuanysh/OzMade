package com.example.ozmade.main.seller.quality

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.seller.quality.data.SellerQualityReviewUi
import com.example.ozmade.main.seller.quality.data.SellerQualityUiState
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerQualityRoute(
    onBack: () -> Unit,
    onOpenProduct: (String) -> Unit,
    viewModel: SellerQualityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.load() }

    SellerQualityScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.load() },
        onOpenProduct = onOpenProduct
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SellerQualityScreen(
    uiState: SellerQualityUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenProduct: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Качество работы") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is SellerQualityUiState.Loading -> {
                Box(
                    Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is SellerQualityUiState.Error -> {
                Column(
                    modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                        Text("Повторить")
                    }
                }
            }

            is SellerQualityUiState.Data -> {
                val d = uiState.data

                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        Text("Показатели качество", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        QualityProgressCard(
                            levelTitle = d.levelTitle,
                            progress = d.levelProgress,
                            hint = d.levelHint
                        )
                    }

                    item {
                        Spacer(Modifier.height(4.dp))
                        Text("Ваши показатели", style = MaterialTheme.typography.titleMedium)
                    }

                    item {
                        RatingCard(
                            rating = d.averageRating,
                            ratingsCount = d.ratingsCount
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Отзывы", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = d.reviewsCount.toString(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    items(d.reviews, key = { it.id }) { r ->
                        ReviewCard(review = r, onOpenProduct = onOpenProduct)
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityProgressCard(levelTitle: String, progress: Float, hint: String) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(levelTitle, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            Text(
                hint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RatingCard(rating: Double, ratingsCount: Int) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarsRow(rating = rating, size = 22.dp)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = String.format("%.1f", rating),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.width(4.dp))
                Text("★", style = MaterialTheme.typography.titleLarge, color = Color(0xFFFFC107))
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "На основе $ratingsCount оценок",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReviewCard(
    review: SellerQualityReviewUi,
    onOpenProduct: (String) -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {

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

            Text(review.text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** 5 звёзд с половинкой */
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
private fun Star(filledFraction: Float, starSize: Dp) {
    val fill = Color(0xFFFFC107)
    val outline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f)
    val path = remember { Path() }

    Canvas(
        modifier = Modifier.size(starSize).padding(end = 4.dp)
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

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

        drawPath(path = path, color = outline, style = Stroke(width = 1.6f))

        if (filledFraction > 0f) {
            clipRect(right = w * filledFraction.coerceIn(0f, 1f)) {
                drawPath(path = path, color = fill, style = Fill)
            }
        }
    }
}