package com.example.ozmade.main.userHome.details

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ozmade.main.user.orderflow.ui.OrderBottomSheet
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
    onOpenDelivery: (Int) -> Unit,
    onOpenReviews: (Int) -> Unit,
    onOpenSeller: (Int) -> Unit,
    onBack: () -> Unit,
) {
    var tab by remember { mutableStateOf(DetailsTab.DESCRIPTION) }
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(pageCount = { max(product.images.size, 1) })

    var showOrderSheet by remember { mutableStateOf(false) }
    var orderQuantity by remember { mutableIntStateOf(1) }

    val topBarAlpha by remember {
        derivedStateOf {
            val threshold = 400f
            (scrollState.value / threshold).coerceIn(0f, 1f)
        }
    }

    Scaffold(
        bottomBar = {
            BottomActionsBar(
                onChat = onChat,
                onOrder = {
                    orderQuantity = 1
                    showOrderSheet = true
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { pageIndex ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (product.images.isNotEmpty()) {
                                val imageUrl = product.images[pageIndex]
                                Log.d("ProductDetailsScreen", "Loading image[$pageIndex]: $imageUrl")
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                    onLoading = { Log.d("ProductDetailsScreen", "Image loading: $imageUrl") },
                                    onSuccess = { Log.d("ProductDetailsScreen", "Image success: $imageUrl") },
                                    onError = {
                                        Log.e("ProductDetailsScreen", "Image error: $imageUrl", it.result.throwable)
                                    }
                                )
                            } else {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.LightGray
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(0.3f))
                                )
                            )
                    )

                    if (product.images.size > 1) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(product.images.size) { i ->
                                    val active = pagerState.currentPage == i
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (active) Color.White
                                                else Color.White.copy(0.4f)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .offset(y = (-20).dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(top = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${product.price} ₸",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onToggleLike) {
                            Icon(
                                if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (liked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleLarge.copy(lineHeight = 28.sp),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB400),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(" ${formatRating(product.rating)} ", fontWeight = FontWeight.Bold)
                        Text(
                            text = "· ${product.reviewsCount} отзывов",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onOpenReviews(product.id) }
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "· ${product.ordersCount} заказов",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
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

                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        if (tab == DetailsTab.DESCRIPTION) {
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            SpecsBlock(product.specs)
                        }
                    }

                    InfoSection(title = "Доставка", icon = Icons.Outlined.LocalShipping) {
                        DeliveryBlock(product.delivery)
                    }

                    InfoSection(title = "Продавец", icon = Icons.Outlined.Storefront) {
                        SellerBlock(product.seller, onClick = { onOpenSeller(product.seller.id) })
                    }

                    Spacer(Modifier.height(100.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .alpha(topBarAlpha)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .shadow(if (topBarAlpha > 0.8f) 4.dp else 0.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (topBarAlpha < 0.5f) Color.Black.copy(0.3f) else Color.Transparent,
                        contentColor = if (topBarAlpha < 0.5f) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }

                if (topBarAlpha > 0.7f) {
                    Text(
                        text = product.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    )
                }
            }
        }
    }

    if (showOrderSheet) {
        OrderBottomSheet(
            title = product.title,
            price = product.price,
            quantity = orderQuantity,
            onMinus = {
                if (orderQuantity > 1) orderQuantity--
            },
            onPlus = {
                orderQuantity++
            },
            onClose = {
                showOrderSheet = false
            },
            onChooseDelivery = {
                showOrderSheet = false
                onOpenDelivery(orderQuantity)
            }
        )
    }
}

@Composable
private fun InfoSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun BottomActionsBar(
    onChat: () -> Unit,
    onOrder: () -> Unit
) {
    Surface(modifier = Modifier.shadow(16.dp), color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onChat,
                modifier = Modifier
                    .weight(0.4f)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Чат", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onOrder,
                modifier = Modifier
                    .weight(0.6f)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Купить сейчас", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val alpha by animateFloatAsState(if (selected) 1f else 0.6f)
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) MaterialTheme.colorScheme.surface else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.alpha(alpha)
        )
    }
}

@Composable
private fun SpecsBlock(specs: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        specs.forEach { (k, v) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = k, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = v,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun DeliveryBlock(delivery: DeliveryInfoUi) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (delivery.freeDeliveryEnabled) {
                DeliveryRow(
                    icon = Icons.Default.Done,
                    label = "Доставка курьером",
                    value = delivery.freeDeliveryText ?: "Бесплатно"
                )
            }
            if (delivery.pickupEnabled) {
                DeliveryRow(
                    icon = Icons.Default.LocationOn,
                    label = "Самовывоз: ${delivery.pickupAddress ?: "не указан"}",
                    value = delivery.pickupTime ?: "Бесплатно"
                )
            }
            if (delivery.pickupEnabled || delivery.freeDeliveryEnabled || delivery.intercityEnabled) {
                // ... logic
            }
            if (delivery.intercityEnabled) {
                DeliveryRow(
                    icon = Icons.Default.Public,
                    label = "Межгород",
                    value = "Доступно"
                )
            }
        }
    }
}

@Composable
private fun DeliveryRow(icon: ImageVector, label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), textAlign = TextAlign.End)
    }
}

@Composable
private fun SellerBlock(seller: SellerUi, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                val avatarUrl = seller.avatarUrl
                if (!avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = seller.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = seller.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = seller.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

private fun formatRating(rating: Double): String {
    return if (rating == 0.0) "Новый" else String.format("%.1f", rating)
}
