package com.example.ozmade.main.home.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ozmade.main.userHome.Product
import com.example.ozmade.main.userHome.category.CategoryUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    uiState: CategoryUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenProduct: (String) -> Unit
) {
    Scaffold { padding ->
        when (uiState) {
            is CategoryUiState.Loading -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is CategoryUiState.Error -> {
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

            is CategoryUiState.Data -> {
                val likedIds = remember { mutableStateListOf<String>() }

                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        // Заголовок категории без фото
                        item(span = { GridItemSpan(2) }) {
                            CategoryHeader(
                                title = uiState.category.title,
                                quote = uiState.headerQuote
                            )
                        }

                        // Товары
                        items(uiState.products, key = { it.id }) { p ->
                            CategoryProductCard(
                                product = p,
                                liked = likedIds.contains(p.id),
                                onToggleLike = {
                                    if (likedIds.contains(p.id)) likedIds.remove(p.id) else likedIds.add(p.id)
                                },
                                onClick = { onOpenProduct(p.id) }
                            )
                        }

                        if (uiState.products.isEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                Text(
                                    text = "В этой категории пока нет товаров",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }

                    // Кнопка назад
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(44.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    title: String,
    quote: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF6A1B9A), Color(0xFFAB47BC))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CategoryProductCard(
    product: Product,
    liked: Boolean,
    onToggleLike: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // квадратные карточки
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                // Кнопка лайк
                IconButton(
                    onClick = onToggleLike,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (liked) Color.Red else Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text("${product.price} ₸", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(
                    product.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${product.city}, ${product.address}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Рейтинг: ${String.format("%.1f", product.rating)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}