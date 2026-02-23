package com.example.ozmade.main.home.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.HorizontalDivider

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
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(pageCount = { max(product.images.size, 1) })

    // Вычисляем прозрачность верхнего бара в зависимости от скролла
    val topBarAlpha by remember {
        derivedStateOf {
            val threshold = 400f
            (scrollState.value / threshold).coerceIn(0f, 1f)
        }
    }

    Scaffold(
        bottomBar = {
            BottomActionsBar(onChat = onChat, onOrder = onOrder)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8F9FA))) {

            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {

                // --- БЛОК С ФОТО (PAGER) ---
                Box(modifier = Modifier.fillMaxWidth().height(380.dp)) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        // Тут позже будет Coil AsyncImage
                        Box(
                            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        }
                    }

                    // Градиент снизу, чтобы индикатор был виден на светлых фото
                    Box(
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(80.dp)
                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.3f))))
                    )

                    // Индикатор
                    if (product.images.size > 1) {
                        Surface(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(product.images.size) { i ->
                                    val active = pagerState.currentPage == i
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (active) Color.White else Color.White.copy(0.4f)))
                                }
                            }
                        }
                    }
                }

                // --- КОНТЕНТ ---
                Column(
                    modifier = Modifier
                        .offset(y = (-20).dp) // Наползание на фото
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color.White)
                        .padding(top = 24.dp)
                ) {
                    // Цена и действия
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${product.price} ₸",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onToggleLike) {
                            Icon(if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null, tint = if (liked) Color.Red else Color.Gray)
                        }
                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Gray)
                        }
                    }

                    // Название
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleLarge.copy(lineHeight = 28.sp),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )

                    // Рейтинг и заказы
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(18.dp))
                        Text(" ${formatRating(product.rating)} ", fontWeight = FontWeight.Bold)
                        Text(
                            text = "· ${product.reviewsCount} отзывов",
                            color = Color.Blue,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onOpenReviews(product.id) }
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("· ${product.ordersCount} заказов", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(Modifier.height(24.dp))

                    // Табы
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(48.dp)
                            .clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F3F5))
                    ) {
                        TabButton(text = "Описание", selected = tab == DetailsTab.DESCRIPTION, onClick = { tab = DetailsTab.DESCRIPTION }, modifier = Modifier.weight(1f))
                        TabButton(text = "Характеристики", selected = tab == DetailsTab.SPECS, onClick = { tab = DetailsTab.SPECS }, modifier = Modifier.weight(1f))
                    }

                    Box(modifier = Modifier.padding(20.dp).fillMaxWidth().animateContentSize()) {
                        if (tab == DetailsTab.DESCRIPTION) {
                            Text(text = product.description, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
                        } else {
                            SpecsBlock(product.specs)
                        }
                    }

                    // Блок доставки
                    InfoSection(title = "Доставка", icon = Icons.Outlined.LocalShipping) {
                        DeliveryBlock(product.delivery)
                    }

                    // Блок продавца
                    InfoSection(title = "Продавец", icon = Icons.Outlined.Storefront) {
                        SellerBlock(product.seller, onClick = { onOpenSeller(product.seller.id) })
                    }

                    Spacer(Modifier.height(100.dp))
                }
            }

            // --- КРАСИВЫЙ TOPBAR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .alpha(topBarAlpha)
                    .background(Color.White.copy(alpha = 0.95f)) // Эффект стекла
                    .shadow(if (topBarAlpha > 0.8f) 4.dp else 0.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (topBarAlpha < 0.5f) Color.Black.copy(0.3f) else Color.Transparent,
                        contentColor = if (topBarAlpha < 0.5f) Color.White else Color.Black
                    )
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, modifier = Modifier.size(32.dp))
                }

                if (topBarAlpha > 0.7f) {
                    Text(
                        text = product.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp).weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
            Spacer(Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun BottomActionsBar(onChat: () -> Unit, onOrder: () -> Unit) {
    Surface(modifier = Modifier.shadow(16.dp), color = Color.White) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onChat,
                modifier = Modifier.weight(0.4f).height(54.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Чат", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onOrder,
                modifier = Modifier.weight(0.6f).height(54.dp),
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
    val alpha by animateFloatAsState(if (selected) 1f else 0f, label = "")
    Box(
        modifier = modifier.fillMaxHeight().padding(4.dp).clip(RoundedCornerShape(10.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, color = if (selected) Color.Black else Color.Gray)
    }
}

@Composable
private fun SpecsBlock(specs: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        specs.forEach { (k, v) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = k, modifier = Modifier.weight(1f), color = Color.Gray)
                Text(text = v, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
            HorizontalDivider(color = Color(0xFFF1F1F1))
        }
    }
}

@Composable
private fun DeliveryBlock(delivery: DeliveryInfoUi) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (delivery.pickupEnabled) DeliveryRow("Самовывоз", delivery.pickupTime ?: "Бесплатно")
            if (delivery.freeDeliveryEnabled) DeliveryRow("Доставка курьером", delivery.freeDeliveryText ?: "Бесплатно")
            if (delivery.intercityEnabled) DeliveryRow("Межгород", "Доступно")
        }
    }
}

@Composable
private fun DeliveryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.DarkGray)
        Text(value, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
    }
}

@Composable
private fun SellerBlock(seller: SellerUi, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8F9FA),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                Text(seller.name.take(1).uppercase(), style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(seller.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(seller.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

private fun formatRating(r: Double): String = String.format("%.1f", r)

