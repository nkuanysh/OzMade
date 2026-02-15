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
import kotlinx.coroutines.delay

// -------------------- модели для примера --------------------

private enum class AppLang { KAZ, RUS }

private data class CategoryUi(
    val id: String,
    val title: String,
)

private data class ProductUi(
    val id: String,
    val title: String,
    val price: Int,
    val city: String,
    val address: String,
    val rating: Double,
)

private val LikedIdsSaver: Saver<MutableList<String>, Any> = listSaver(
    save = { it.toList() },
    restore = { restored -> mutableStateListOf<String>().apply { addAll(restored) } }
)

// -------------------- главный экран --------------------

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {
    var lang by rememberSaveable { mutableStateOf(AppLang.RUS) }
    var search by rememberSaveable { mutableStateOf("") }

    val ads = remember { listOf("ad_1", "ad_2", "ad_3", "ad_4", "ad_5") }

    val categories = remember {
        listOf(
            CategoryUi("food", "Еда"),
            CategoryUi("clothes", "Одежда"),
            CategoryUi("art", "Искусство"),
            CategoryUi("craft", "Ремесло"),
            CategoryUi("gifts", "Подарки"),
            CategoryUi("holidays", "Праздники"),
            CategoryUi("home", "Для дома"),
        )
    }

    val products = remember {
        listOf(
            ProductUi("1", "Домашний сыр", 2500, "Алматы", "Алмалинский р-н", 4.8),
            ProductUi("2", "Тойбастар набор", 5500, "Алматы", "Ауэзовский р-н", 4.6),
            ProductUi("3", "Кукла ручной работы", 12000, "Шымкент", "Центр", 4.9),
            ProductUi("4", "Наурыз-көже", 1500, "Тараз", "Мкр. 12", 4.5),
            ProductUi("5", "Свитшот", 9900, "Астана", "Сарыарка", 4.3),
            ProductUi("6", "Картина (арт)", 30000, "Алматы", "Бостандык", 4.7),
            ProductUi("7", "Пельмени домашние", 2800, "Алматы", "Медеуский р-н", 4.4),
            ProductUi("8", "Букет к 8 марта", 7000, "Алматы", "Жетысу", 4.9),
        )
    }

    val filteredProducts = remember(search, products) {
        val q = search.trim()
        if (q.isEmpty()) products
        else products.filter {
            it.title.contains(q, ignoreCase = true) ||
                    it.city.contains(q, ignoreCase = true)
        }
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { ads.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val next = (pagerState.currentPage + 1) % ads.size
            pagerState.animateScrollToPage(next)
        }
    }

    val likedIds = rememberSaveable(saver = LikedIdsSaver) {
        mutableStateListOf<String>()
    }

    val gridState = rememberLazyGridState()

    // ✅ Поиск закрепляем сверху оверлеем, а весь контент скроллится одним LazyVerticalGrid
    Box(Modifier.fillMaxSize()) {

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // чтобы контент не залезал под закреплённый поиск
            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(86.dp)) }

            // Языки (2 колонки)
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

            // Название (2 колонки)
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "OzMade",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                )
            }

            // Реклама (2 колонки)
            item(span = { GridItemSpan(2) }) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 6.dp),
                    pageSpacing = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(top = 10.dp)
                ) { page ->
                    AdBanner(
                        title = "Реклама ${page + 1}",
                        onClick = { /* TODO */ }
                    )
                }
            }

            // Категории (2 колонки)
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Категории",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            // Категории горизонтально (2 колонки)
            item(span = { GridItemSpan(2) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(categories) { cat ->
                        CategoryChip(
                            title = cat.title,
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }

            // Товары (по 2 в ряд)
            items(filteredProducts, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    liked = likedIds.contains(product.id),
                    onToggleLike = {
                        if (likedIds.contains(product.id)) likedIds.remove(product.id)
                        else likedIds.add(product.id)
                    },
                    onClick = { /* TODO */ }
                )
            }
        }

        // ✅ Поиск остаётся сверху
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

// -------------------- компоненты --------------------

@Composable
private fun LangChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = AssistChipDefaults.assistChipColors(
        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        labelColor = MaterialTheme.colorScheme.onSurface
    )
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        colors = colors
    )
}

@Composable
private fun AdBanner(
    title: String,
    onClick: () -> Unit
) {
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
private fun CategoryChip(
    title: String,
    onClick: () -> Unit
) {
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
    product: ProductUi,
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
