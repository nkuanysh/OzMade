package com.example.ozmade.main

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
import androidx.compose.material.icons.Icons
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
import com.example.ozmade.main.home.*
import kotlinx.coroutines.delay

private enum class AppLang { KAZ, RUS }

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

    var lang by rememberSaveable { mutableStateOf(AppLang.RUS) }
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

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            // отступ под закреплённый поиск
            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(86.dp)) }

            // Языки
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LangChip("Қаз", lang == AppLang.KAZ) { lang = AppLang.KAZ }
                    Spacer(Modifier.width(8.dp))
                    LangChip("Рус", lang == AppLang.RUS) { lang = AppLang.RUS }
                }
            }

            // Название
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "OzMade",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        ,
                )
            }

            // Реклама
            item(span = { GridItemSpan(2) }) {
                when (uiState) {
                    is HomeUiState.Loading -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    is HomeUiState.Error -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            val msg = (uiState as HomeUiState.Error).message
                            Column(
                                Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(msg, color = MaterialTheme.colorScheme.error)
                                Spacer(Modifier.height(10.dp))
                                Button(onClick = { viewModel.load() }) { Text("Повторить") }
                            }
                        }
                    }

                    is HomeUiState.Data -> {
                        HorizontalPager(
                            state = pagerState,
                            contentPadding = PaddingValues(horizontal = 6.dp),
                            pageSpacing = 12.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(top = 10.dp)
                        ) { page ->
                            val banner = ads.getOrNull(page)
                            AdBannerCard(
                                title = banner?.title ?: "Реклама",
                                onClick = { /* TODO: открыть deeplink banner?.deeplink */ }
                            )
                        }
                    }
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
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(categories) { cat ->
                        CategoryChip(
                            title = cat.title,
                            onClick = { onOpenCategory(cat.id) }
                        )
                    }
                }
            }

            // Товары
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

        // Поиск закреплён сверху
        Surface(
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск товаров или города") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            }
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
private fun AdBannerCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun CategoryChip(title: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
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
                    text = "Рейтинг: ${product.rating}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
