package com.example.ozmade.main.userHome.seller

import android.util.Log
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerScreen(
    uiState: SellerUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSearchChanged: (String) -> Unit,
    onToggleLike: (Int) -> Unit,
    onOpenProduct: (Int) -> Unit,
    onOpenSellerReviews: (Int) -> Unit,
) {
    var search by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Продавец", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        when (uiState) {
            is SellerUiState.Loading -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
            }

            is SellerUiState.Error -> {
                Column(
                    modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 32.dp
                    ),
                    modifier = Modifier.padding(padding).fillMaxSize()
                ) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        SellerHeaderBlock(
                            seller = seller,
                            onOpenReviews = { onOpenSellerReviews(seller.id) }
                        )
                    }

                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        OutlinedTextField(
                            value = search,
                            onValueChange = {
                                search = it
                                onSearchChanged(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Поиск в магазине") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFFF9800)) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF9800),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

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
) {
    // Определяем имя для показа (игнорируем числовые названия магазинов типа "47")
    val displayName = remember(seller.name, seller.storeName) {
        val sName = seller.storeName ?: ""
        if (sName.isBlank() || sName.all { it.isDigit() }) {
            seller.name
        } else {
            sName
        }
    }

    Surface(
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFFFFF9F0),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Аватар
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0E6FF)),
                contentAlignment = Alignment.Center
            ) {
                if (!seller.photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = seller.photoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        onError = { Log.e("SellerUI", "Avatar fail: ${seller.photoUrl}") }
                    )
                } else {
                    Text(
                        text = displayName.take(1).uppercase(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF673AB7)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Если отображаем название магазина, можно показать имя мастера ниже
            if (displayName != seller.name && seller.name.isNotBlank()) {
                Text(
                    text = "Мастер: ${seller.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            // Статус
            val (statusText, statusColor) = when (seller.status.lowercase()) {
                "pending" -> "Проверяется" to Color(0xFFFF9800)
                "active" -> "Активен" to Color(0xFF4CAF50)
                else -> (if (seller.status.isBlank()) "Новый мастер" else seller.status) to Color(0xFF607D8B)
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.15f),
                contentColor = statusColor
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Статистика
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(value = "${seller.ordersCount}", label = "заказов", modifier = Modifier.weight(1f))
                
                Box(Modifier.width(1.dp).height(30.dp).background(Color.LightGray.copy(0.5f)))
                
                StatItem(
                    value = String.format("%.1f", seller.rating),
                    label = "рейтинг",
                    modifier = Modifier.weight(1f).clickable { onOpenReviews() },
                    isHighlight = true
                )

                Box(Modifier.width(1.dp).height(30.dp).background(Color.LightGray.copy(0.5f)))

                StatItem(value = "${seller.daysWithOzMade}", label = "дней", modifier = Modifier.weight(1f))
            }

            if (!seller.city.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(" ${seller.city}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, modifier: Modifier = Modifier, isHighlight: Boolean = false) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = if (isHighlight) Color(0xFFFF9800) else Color.Black
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
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
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.9f)) {
                if (!product.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        onError = { Log.e("SellerUI", "Product img fail: ${product.imageUrl}") }
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Image, null, modifier = Modifier.size(40.dp), tint = Color.LightGray)
                    }
                }
                
                Surface(
                    onClick = onToggleLike,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp),
                    shape = CircleShape,
                    color = Color.White.copy(0.9f)
                ) {
                    Icon(
                        if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null,
                        modifier = Modifier.padding(7.dp),
                        tint = if (liked) Color.Red else Color.Gray
                    )
                }
            }

            Column(Modifier.padding(12.dp)) {
                Text("${product.price} ₸", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(product.title, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                
                Spacer(Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB400))
                    Text(" ${product.rating}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
