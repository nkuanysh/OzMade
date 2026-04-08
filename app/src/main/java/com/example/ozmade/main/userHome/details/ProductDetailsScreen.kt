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
import androidx.compose.material.icons.automirrored.filled.Chat
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
    onOpenDelivery: (Int) -> Unit, // qty
    onOpenReviews: (Int) -> Unit,
    onOpenSeller: (Int) -> Unit,
    onBack: () -> Unit,
) {
    var tab by remember { mutableStateOf(DetailsTab.DESCRIPTION) }
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(pageCount = { max(product.images.size, 1) })

    var showOrderSheet by remember { mutableStateOf(false) }
    var orderQuantity by remember { mutableIntStateOf(1) }

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
                .background(Color(0xFFF8F9FA))
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
                        .background(Color.White)
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
                                tint = if (liked) Color.Red else Color.Gray
                            )
                        }
                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Gray)
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
                            color = Color.Blue,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onOpenReviews(product.id) }
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "· ${product.ordersCount} заказов",
                            color = Color.Gray,
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
                            .background(Color(0xFFF1F3F5))
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
                                color = Color.DarkGray
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

            // Top Bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.3f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }

    if (showOrderSheet) {
        OrderBottomSheet(
            title = product.title,
            price = product.price.toDouble(),
            quantity = orderQuantity,
            onMinus = { if (orderQuantity > 1) orderQuantity-- },
            onPlus = { orderQuantity++ },
            onClose = { showOrderSheet = false },
            onChooseDelivery = { 
                showOrderSheet = false
                onOpenDelivery(orderQuantity)
            }
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(if (selected) 1f else 0.4f)
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.alpha(alpha)
        )
    }
}

@Composable
private fun InfoSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(top = 32.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.Gray)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun SpecsBlock(specs: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        specs.forEach { (key, value) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(key, modifier = Modifier.weight(1f), color = Color.Gray)
                Text(value, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun DeliveryBlock(delivery: DeliveryInfoUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1F3F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (delivery.freeDeliveryEnabled) {
            DeliveryItem(Icons.Default.Done, delivery.freeDeliveryText ?: "Бесплатная доставка")
        }
        if (delivery.pickupEnabled) {
            DeliveryItem(Icons.Default.LocationOn, "Самовывоз: ${delivery.pickupAddress ?: "не указан"}")
            if (!delivery.pickupTime.isNullOrBlank()) {
                DeliveryItem(Icons.Default.AccessTime, "Время: ${delivery.pickupTime}")
            }
        }
        if (delivery.intercityEnabled) {
            DeliveryItem(Icons.Default.Public, "Межгород доступен")
        }
    }
}

@Composable
private fun DeliveryItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SellerBlock(seller: SellerUi, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = Color.Transparent
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = Color(0xFFE9ECEF)
            ) {
                AsyncImage(
                    model = seller.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(seller.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(seller.address, color = Color.Gray, fontSize = 14.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
private fun BottomActionsBar(onChat: () -> Unit, onOrder: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onChat,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Чат")
            }
            Button(
                onClick = onOrder,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Заказать", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatRating(rating: Double): String {
    return if (rating == 0.0) "Новый" else "%.1f".format(rating)
}
