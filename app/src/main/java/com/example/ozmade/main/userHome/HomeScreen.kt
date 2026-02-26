package com.example.ozmade.main.userHome

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

fun categoryIcon(id: String): ImageVector {
    return when (id) {
        "food" -> Icons.Default.Restaurant
        "clothes" -> Icons.Default.Checkroom
        "art" -> Icons.Default.Palette
        "craft" -> Icons.Default.Handyman
        "gifts" -> Icons.Default.CardGiftcard
        "holiday" -> Icons.Default.Celebration
        "home" -> Icons.Default.Chair
        else -> Icons.Default.Category
    }
}
//private enum class AppLang { KAZ, RUS }

private val LikedIdsSaver: Saver<MutableList<String>, Any> = listSaver(
    save = { it.toList() },
    restore = { restored -> mutableStateListOf<String>().apply { addAll(restored) } }
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onOpenProduct: (String) -> Unit,
    onOpenCategory: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

//    var lang by rememberSaveable { mutableStateOf(AppLang.RUS) }
    var search by rememberSaveable { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    val likedIds = rememberSaveable(saver = LikedIdsSaver) { mutableStateListOf<String>() }
    val gridState = rememberLazyGridState()

    // Достаём данные из state
    val data = uiState as? HomeUiState.Data
    val ads = data?.ads.orEmpty()
    val categories = data?.categories.orEmpty()
    val products = data?.products.orEmpty()

    val filteredProducts = remember(search, products) {
        val q = search.trim()
        if (q.isEmpty()) products
        else products.filter {
            it.title.contains(q, ignoreCase = true) ||
                    it.city.contains(q, ignoreCase = true)
        }
    }

    // Pager state должен знать количество страниц
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { maxOf(ads.size, 1) })

    // Автопрокрутка рекламы только если баннеров >= 2
    LaunchedEffect(ads.size) {
        if (ads.size < 2) return@LaunchedEffect
        while (true) {
            delay(3000)
            val next = (pagerState.currentPage + 1) % ads.size
            pagerState.animateScrollToPage(next)
        }
    }

    Box(Modifier.fillMaxSize()) {

        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка: ${state.message}")
                }
            }

            is HomeUiState.Data -> {
                val ads = state.ads
                val categories = state.categories
                val products = state.products

                val filteredProducts = remember(search, products) {
                    val q = search.trim()
                    if (q.isEmpty()) products
                    else products.filter {
                        it.title.contains(q, ignoreCase = true) ||
                                it.city.contains(q, ignoreCase = true)
                    }
                }

                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(86.dp)) }

                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = "OzMade",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                        )
                    }

                    // Реклама
                    item(span = { GridItemSpan(2) }) {
                        if (ads.isNotEmpty()) {
                            val pagerStateAds = rememberPagerState(
                                initialPage = 0,
                                pageCount = { ads.size }
                            )

                            LaunchedEffect(ads.size) {
                                if (ads.size > 1) {
                                    while (true) {
                                        delay(4000)
                                        val next = (pagerStateAds.currentPage + 1) % ads.size
                                        pagerStateAds.animateScrollToPage(next)
                                    }
                                }
                            }

                            HorizontalPager(
                                state = pagerStateAds,
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                pageSpacing = 12.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            ) { page ->
                                AdBannerCard(
                                    ad = ads[page],
                                    onClick = { /* TODO */ }
                                )
                            }
                        } else {
                            // на всякий: если всё-таки пусто
                            Text("Реклама скоро появится", color = Color.Gray, modifier = Modifier.padding(8.dp))
                        }
                    }

// Категории заголовок
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = "Категории",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

// Категории горизонтально
                    item(span = { GridItemSpan(2) }) {
                        if (categories.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                items(categories) { cat ->
                                    CategoryChip(
                                        title = cat.title,
                                        icon = categoryIcon(cat.id),
                                        onClick = { onOpenCategory(cat.id) }
                                    )
                                }
                            }
                        } else {
                            Text("Категории скоро появятся", color = Color.Gray, modifier = Modifier.padding(8.dp))
                        }
                    }

                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            liked = likedIds.contains(product.id),
                            onToggleLike = {
                                if (likedIds.contains(product.id)) likedIds.remove(product.id)
                                else likedIds.add(product.id)
                            },
                            onClick = { onOpenProduct(product.id) }
                        )
                    }
                }
            }
        }

        // ✅ Поиск рисуем поверх ВСЕХ состояний (или можешь оставить только для Data)
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter) // теперь снова BoxScope → работает
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(56.dp),
                placeholder = { Text("Поиск товаров или города") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { search = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Очистить")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

// -------------------- компоненты UI --------------------

@Composable
private fun LangChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = AssistChipDefaults.assistChipColors(
        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        labelColor = MaterialTheme.colorScheme.onSurface
    )
    AssistChip(onClick = onClick, label = { Text(text) }, colors = colors)
}

@Composable
private fun AdBannerCard(ad: AdBanner, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()       // ширина по экрану
            .height(160.dp)       // фиксированная высота
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Картинка
            ad.imageRes?.let { res ->
                Image(
                    painter = painterResource(id = res),
                    contentDescription = ad.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Текст поверх картинки
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = ad.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )

        Spacer(Modifier.width(6.dp))

        Text(text = title)
    }
}

@Composable
private fun ProductCard(
    product: Product,
    liked: Boolean,
    onToggleLike: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Изображение товара
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Placeholder для изображения
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                )

                // Кнопка "лайк" сверху
                IconButton(
                    onClick = onToggleLike,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
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

            Column(modifier = Modifier.padding(12.dp)) {
                // Цена
                Text(
                    text = "${product.price} ₸",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(4.dp))

                // Название товара
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                // Адрес
                Text(
                    text = "${product.city}, ${product.address}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                // Рейтинг
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${product.rating}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}