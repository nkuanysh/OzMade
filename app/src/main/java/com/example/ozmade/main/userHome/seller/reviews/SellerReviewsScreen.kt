package com.example.ozmade.main.userHome.seller.reviews

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ozmade.utils.formatRating
import com.example.ozmade.main.userHome.reviews.StarsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerReviewsScreen(
    uiState: SellerReviewsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenProduct: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when (uiState) {
                        is SellerReviewsUiState.Data -> "Отзывы о продавце (${uiState.header.reviewsCount})"
                        else -> "Отзывы о продавце"
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
            is SellerReviewsUiState.Loading -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(strokeWidth = 3.dp) }
            }

            is SellerReviewsUiState.Error -> {
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

            is SellerReviewsUiState.Data -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    item {
                        SellerReviewsHeader(header = uiState.header)
                    }

                    item {
                        HorizontalDivider(
                            thickness = 8.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    }

                    items(uiState.reviews, key = { it.id }) { r ->
                        SellerReviewCard(
                            review = r,
                            onOpenProduct = onOpenProduct
                        )
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
private fun SellerReviewsHeader(header: SellerReviewsHeaderUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Text(
            text = header.sellerName,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatRating(header.averageRating),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                StarsRow(rating = header.averageRating, size = 18.dp)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${header.ratingsCount} оценок",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(32.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RatingBar(5, 0.85f)
                RatingBar(4, 0.10f)
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
private fun SellerReviewCard(
    review: SellerReviewUi,
    onOpenProduct: (Int) -> Unit
) {
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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = review.userName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.userName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = review.dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            StarsRow(rating = review.rating, size = 14.dp)
        }

        Spacer(Modifier.height(8.dp))
        
        // Product reference
        Surface(
            onClick = { onOpenProduct(review.productId) },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Товар: ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = review.productTitle,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = review.text,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
