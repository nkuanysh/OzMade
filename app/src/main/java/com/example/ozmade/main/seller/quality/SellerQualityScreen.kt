package com.example.ozmade.main.seller.quality

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.seller.quality.data.SellerQualityReviewUi
import com.example.ozmade.main.seller.quality.data.SellerQualityUiState
import java.util.Locale
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerQualityRoute(
    onBack: () -> Unit,
    onOpenProduct: (Int) -> Unit,
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
    onOpenProduct: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Качество и рейтинг", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        when (uiState) {
            is SellerQualityUiState.Loading -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Color(0xFFFF9800)) }
            }

            is SellerQualityUiState.Error -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(24.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Повторить")
                    }
                }
            }

            is SellerQualityUiState.Data -> {
                val d = uiState.data

                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "ВАШ УРОВЕНЬ",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        QualityProgressCard(
                            levelTitle = d.levelTitle,
                            progress = d.levelProgress,
                            hint = d.levelHint
                        )
                    }

                    item {
                        Text(
                            "ОЦЕНКА МАГАЗИНА",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        RatingCard(
                            rating = d.averageRating,
                            ratingsCount = d.ratingsCount
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "ОТЗЫВЫ ПОКУПАТЕЛЕЙ",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Gray
                            )
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFFF9800).copy(alpha = 0.1f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = d.reviewsCount.toString(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (d.reviews.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Пока нет отзывов", color = Color.Gray)
                            }
                        }
                    } else {
                        items(d.reviews, key = { it.id }) { r ->
                            ReviewCard(review = r, onOpenProduct = onOpenProduct)
                        }
                    }
                    
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun QualityProgressCard(levelTitle: String, progress: Float, hint: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFFE8F5E9)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = Color(0xFF2E7D32)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(levelTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF43A047),
                trackColor = Color(0xFFE8F5E9)
            )
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                hint,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun RatingCard(rating: Double, ratingsCount: Int) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format(Locale.US, "%.1f", rating),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = Color(0xFF212121)
            )
            
            StarsRow(rating = rating, size = 24.dp)
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "на основе $ratingsCount оценок",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ReviewCard(
    review: SellerQualityReviewUi,
    onOpenProduct: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = Color(0xFFF5F5F5)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(review.userName.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = review.userName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = review.dateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                StarsRow(rating = review.rating, size = 14.dp)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF424242)
            )

            Spacer(Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenProduct(review.productId) }
                    .background(Color(0xFFF8F9FA))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.Transparent
            ) {
                Text(
                    text = "Товар: ${review.productTitle}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF5C6BC0),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

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
    val outline = Color.LightGray.copy(alpha = 0.5f)
    val path = remember { Path() }

    Canvas(
        modifier = Modifier.size(starSize).padding(end = 2.dp)
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
