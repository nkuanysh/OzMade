package com.example.ozmade.main.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerScreen(
    uiState: SellerUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSearchChanged: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onOpenProduct: (String) -> Unit,
    onOpenSellerReviews: (String) -> Unit,

    ) {
    var search by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Продавец") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        when (uiState) {
            is SellerUiState.Loading -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is SellerUiState.Error -> {
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

            is SellerUiState.Data -> {
                val seller = uiState.seller
                val products = uiState.products

                val filteredProducts = remember(search, products) {
                    val q = search.trim()
                    if (q.isEmpty()) products
                    else products.filter {
                        it.title.contains(q, ignoreCase = true) ||
                                it.city.contains(q, ignoreCase = true)
                    }
                }

                val gridState = rememberLazyGridState()

                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    ),
                    modifier = Modifier.padding(padding).fillMaxSize()
                ) {
                    // Блок продавца (2 колонки)
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        SellerHeaderBlock(
                            seller = seller,
                            onOpenReviews = { onOpenSellerReviews(seller.id) }
                        )

                    }

                    // Поиск (2 колонки)
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        OutlinedTextField(
                            value = search,
                            onValueChange = {
                                search = it
                                onSearchChanged(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Поиск товаров продавца") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                    }

                    // Товары
                    items(filteredProducts, key = { it.id }) { p ->
                        SellerProductCard(
                            product = p,
                            liked = uiState.likedIds.contains(p.id),
                            onToggleLike = { onToggleLike(p.id) },
                            onClick = { onOpenProduct(p.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerHeaderBlock(
    seller: SellerHeaderUi,
    onOpenReviews: () -> Unit
)
 {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {

            // аватар + имя по центру
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = seller.name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = seller.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = seller.status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(14.dp))

            // Статистика: слева заказы, центр рейтинг/отзывы, справа дни
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                StatColumn(
                    title = "${seller.ordersCount}",
                    subtitle = "заказов",
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenReviews)
                ) {
                    StatColumn(
                        title = String.format("%.1f", seller.rating),
                        subtitle = "${seller.reviewsCount} отзывов",
                        modifier = Modifier.fillMaxWidth(),
                        center = true
                    )
                }


                StatColumn(
                    title = "${seller.daysWithOzMade}",
                    subtitle = "дней с OzMade",
                    modifier = Modifier.weight(1f),
                    right = true
                )
            }
        }
    }
}

@Composable
private fun StatColumn(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    center: Boolean = false,
    right: Boolean = false
) {
    val align = when {
        right -> Alignment.End
        center -> Alignment.CenterHorizontally
        else -> Alignment.Start
    }
    Column(modifier = modifier, horizontalAlignment = align) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(2.dp))
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SellerProductCard(
    product: SellerProductUi,
    liked: Boolean,
    onToggleLike: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                )

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
                        contentDescription = null
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "${product.price} ₸",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${product.city}, ${product.address}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Рейтинг: ${String.format("%.1f", product.rating)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
