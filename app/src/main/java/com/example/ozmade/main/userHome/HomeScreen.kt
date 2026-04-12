package com.example.ozmade.main.userHome

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.example.ozmade.utils.formatRating
import com.example.ozmade.R

@Composable
fun HomeScreen(
    uiState: HomeUiState = HomeUiState.Loading,
    onSearchClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onOpenCategory: (String) -> Unit = {},
    onOpenProduct: (Int) -> Unit = {},
    onFavoriteClick: (Int) -> Unit = {},
    onSeeAllCategoriesClick: () -> Unit = {},
    onSeeAllProductsClick: () -> Unit = {},
    onTabSelected: (HomeTab) -> Unit = {},
    onAdClick: (AdBanner) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val orangeAccent = Color(0xFFFF9800)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor
    ) { paddingValues ->
        when (uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = orangeAccent)
                }
            }
            is HomeUiState.Error -> {
                ErrorContent(message = uiState.message, onRetry = onRetry, accentColor = orangeAccent)
            }
            is HomeUiState.Data -> {
                val displayProducts = when (uiState.selectedTab) {
                    HomeTab.ALL_PRODUCTS -> {
                        if (uiState.searchQuery.isBlank()) uiState.products
                        else uiState.products.filter { it.title.contains(uiState.searchQuery, ignoreCase = true) }
                    }
                    HomeTab.RECOMMENDATIONS -> uiState.recommendations
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        HomeSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange
                        )
                    }

                    if (uiState.searchQuery.isBlank()) {
                        if (uiState.ads.isNotEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                ModernPromoSlider(
                                    ads = uiState.ads,
                                    accentColor = orangeAccent,
                                    onAdClick = onAdClick
                                )
                            }
                        }

                        item(span = { GridItemSpan(2) }) {
                            SectionHeader(
                                title = stringResource(R.string.all_categories),
                                actionText = null,
                                onActionClick = onSeeAllCategoriesClick,
                                accentColor = orangeAccent
                            )
                        }

                        item(span = { GridItemSpan(2) }) {
                            CategoriesHorizontalList(onCategoryClick = onOpenCategory, accentColor = orangeAccent)
                        }

                        item(span = { GridItemSpan(2) }) {
                            HomeTabSelector(
                                selectedTab = uiState.selectedTab,
                                onTabSelected = onTabSelected,
                                accentColor = orangeAccent
                            )
                        }
                    } else {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = stringResource(R.string.search_results, uiState.searchQuery),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    items(displayProducts) { product ->
                        MarketProductCard(
                            product = product,
                            onClick = { onOpenProduct(product.id) },
                            onFavoriteClick = { onFavoriteClick(product.id) },
                            accentColor = orangeAccent
                        )
                    }

                    if (displayProducts.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    if (uiState.searchQuery.isNotBlank()) stringResource(R.string.nothing_found) 
                                    else if (uiState.selectedTab == HomeTab.RECOMMENDATIONS) stringResource(R.string.no_recommendations)
                                    else stringResource(R.string.no_products),
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTabSelector(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onTabSelected(HomeTab.ALL_PRODUCTS) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.all_products_tab),
                fontSize = 16.sp,
                fontWeight = if (selectedTab == HomeTab.ALL_PRODUCTS) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == HomeTab.ALL_PRODUCTS) accentColor else Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(if (selectedTab == HomeTab.ALL_PRODUCTS) accentColor else Color.Transparent)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onTabSelected(HomeTab.RECOMMENDATIONS) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.recommendations_tab),
                fontSize = 16.sp,
                fontWeight = if (selectedTab == HomeTab.RECOMMENDATIONS) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == HomeTab.RECOMMENDATIONS) accentColor else Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(if (selectedTab == HomeTab.RECOMMENDATIONS) accentColor else Color.Transparent)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text(stringResource(R.string.search_placeholder), color = Color.Gray, fontSize = 15.sp) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.LightGray) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Rounded.Close, contentDescription = null, tint = Color.Gray)
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernPromoSlider(ads: List<AdBanner>, accentColor: Color, onAdClick: (AdBanner) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { ads.size })
        LaunchedEffect(Unit) {
            while (true) {
                kotlinx.coroutines.delay(2500)
                val nextPage = (pagerState.currentPage + 1) % ads.size
                pagerState.animateScrollToPage(nextPage)
            }
        }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.1f)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            val ad = ads[page]
            Box(modifier = Modifier.fillMaxSize().clickable { onAdClick(ad) }) {
                if (ad.imageUrl != null) {
                    AsyncImage(
                        model = ad.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (ad.imageRes != null) {
                    Image(
                        painter = painterResource(id = ad.imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            repeat(ads.size) { iteration ->
                val active = pagerState.currentPage == iteration
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(if (active) accentColor else Color.LightGray.copy(alpha = 0.5f))
                        .size(if (active) 8.dp else 6.dp)
                )
            }
        }
    }
}

@Composable
fun CategoriesHorizontalList(onCategoryClick: (String) -> Unit, accentColor: Color) {
    val categories = listOf(
        Category("food", "Еда"),
        Category("clothes", "Одежда"),
        Category("art", "Искусство"),
        Category("craft", "Ремесло"),
        Category("gifts", "Подарки"),
        Category("home", "Для дома")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(categories) { category ->
            MarketCategoryChip(
                category = category,
                onClick = { onCategoryClick(category.id) },
                accentColor = accentColor
            )
        }
    }
}

@Composable
private fun MarketCategoryChip(category: Category, onClick: () -> Unit, accentColor: Color) {
    val icon = when (category.id) {
        "food" -> Icons.Rounded.Restaurant
        "clothes" -> Icons.Rounded.Checkroom
        "art" -> Icons.Rounded.Palette
        "craft" -> Icons.Rounded.Handyman
        "gifts" -> Icons.Rounded.CardGiftcard
        "home" -> Icons.Rounded.Chair
        else -> Icons.Rounded.Category
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = accentColor
            )
            Text(
                text = category.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MarketProductCard(product: Product, onClick: () -> Unit, onFavoriteClick: () -> Unit, accentColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(34.dp)
                        .clickable { onFavoriteClick() },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (product.liked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = if (product.liked) Color(0xFFF44336) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${product.price.toInt()} ₸",
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatRating(product.rating),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    if (product.ordersCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${stringResource(R.string.orders_count_simple, product.ordersCount)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String?, onActionClick: () -> Unit, accentColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        if (actionText != null) {
            Text(
                text = actionText,
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit, accentColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = accentColor)) {
            Text(stringResource(R.string.try_again_btn))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(uiState = HomeUiState.Data(
        products = listOf(),
        categories = listOf(),
        ads = listOf(AdBanner("1", title = "Скидки дня"))
    ))
}