package com.example.ozmade.main.home.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.max

private enum class DetailsTab { DESCRIPTION, SPECS }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: ProductDetailsUi,
    liked: Boolean,
    onToggleLike: () -> Unit,
    onShare: () -> Unit,
    onChat: () -> Unit,
    onOrder: () -> Unit,
    onOpenReviews: (String) -> Unit,
    onOpenSeller: (String) -> Unit,
    onBack: () -> Unit
) {
    var tab by remember { mutableStateOf(DetailsTab.DESCRIPTION) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { max(product.images.size, 1) }
    )

    val scrollState = rememberScrollState()

    val showTopBar by remember {
        derivedStateOf { scrollState.value > 500 } // можешь подогнать
    }

    Scaffold(
        bottomBar = {
            BottomActionsBar(
                onChat = onChat,
                onOrder = onOrder
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // 1) СКРОЛЛЯЩИЙСЯ КОНТЕНТ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {

                // -------- Фото --------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.tertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Фото ${page + 1}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    // ✅ Back поверх фото (пока topbar не показан)
                    if (!showTopBar) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(44.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }

                    // Индикатор страниц
                    if (product.images.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(product.images.size) { i ->
                                val active = pagerState.currentPage == i
                                Box(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .width(if (active) 18.dp else 6.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (active) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // -------- Цена + кнопки справа --------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.price} ₸",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onToggleLike) {
                        Icon(
                            imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }
                }

                Spacer(Modifier.height(10.dp))

                // -------- Название + рейтинг --------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(10.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "★ ${formatRating(product.rating)}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "(${product.reviewsCount} отзывов)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.Underline
                            ),
                            color = Color.Blue,
                            modifier = Modifier.clickable { onOpenReviews(product.id) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Заказов: ${product.ordersCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(16.dp))

                // -------- Tabs --------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    TabButton(
                        text = "Описание",
                        selected = tab == DetailsTab.DESCRIPTION,
                        onClick = { tab = DetailsTab.DESCRIPTION },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Характеристики",
                        selected = tab == DetailsTab.SPECS,
                        onClick = { tab = DetailsTab.SPECS },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                when (tab) {
                    DetailsTab.DESCRIPTION -> {
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    DetailsTab.SPECS -> {
                        SpecsBlock(
                            specs = product.specs,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // -------- Доставка --------
                DeliveryBlock(
                    delivery = product.delivery,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(14.dp))

                SellerBlock(
                    seller = product.seller,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable { onOpenSeller(product.seller.id) }
                )

                Spacer(Modifier.height(90.dp))
            }

            // 2) ✅ Липкий верхний бар появляется после скролла
            if (showTopBar) {
                Surface(
                    tonalElevation = 6.dp,
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = Color.White
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),

                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                            }
                            Text(
                                text = product.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
//                        Divider()
                    }
                }
            }
        }
    }
}


@Composable
private fun BottomActionsBar(
    onChat: () -> Unit,
    onOrder: () -> Unit
) {
    Surface(tonalElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onChat,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Чат")
            }
            Button(
                onClick = onOrder,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Заказать")
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val content =
        if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(container)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = content, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SpecsBlock(specs: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(16.dp), modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            if (specs.isEmpty()) {
                Text(
                    text = "Характеристики пока не указаны",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                specs.forEachIndexed { index, (k, v) ->
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = k,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = v,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (index != specs.lastIndex) {
                        Spacer(Modifier.height(10.dp))
                        Divider()
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryBlock(delivery: DeliveryInfoUi, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(16.dp), modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text("Доставка", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            var any = false

            if (delivery.pickupEnabled) {
                any = true
                Text(
                    text = "Самовывоз: ${delivery.pickupTime ?: "есть"}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(8.dp))
            }

            if (delivery.freeDeliveryEnabled) {
                any = true
                Text(
                    text = "Бесплатная доставка ${delivery.freeDeliveryText ?: ""}".trim(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(8.dp))
            }

            if (delivery.intercityEnabled) {
                any = true
                Text(
                    text = "Межгород: есть",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(8.dp))
            }

            if (!any) {
                Text(
                    text = "Условия доставки не указаны",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SellerBlock(
    seller: SellerUi,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар (пока заглушка)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                // позже можно Coil AsyncImage по seller.avatarUrl
                Text(
                    text = seller.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = seller.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = seller.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Рейтинг продавца: ${formatRating(seller.rating)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Выполнено заказов: ${seller.completedOrders}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


private fun formatRating(r: Double): String {
    // 4.8 -> "4.8", 4.0 -> "4.0"
    return String.format("%.1f", r)
}
