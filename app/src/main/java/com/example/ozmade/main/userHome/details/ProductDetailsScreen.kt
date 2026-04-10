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
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.ozmade.main.user.orderflow.ui.OrderBottomSheet
import kotlin.math.max
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

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
    
    val videoId = remember(product.youtubeUrl) { extractYoutubeVideoId(product.youtubeUrl) }
    val hasVideo = videoId != null
    val totalItems = product.images.size + (if (hasVideo) 1 else 0)
    
    val pagerState = rememberPagerState(pageCount = { max(totalItems, 1) })

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
                // Image/Video Pager
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
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
                            if (pageIndex < product.images.size) {
                                AsyncImage(
                                    model = product.images[pageIndex],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (hasVideo && pageIndex == product.images.size) {
                                YoutubeVideoSlide(videoId!!)
                            }
                        }
                    }

                    // Top gradient for back button visibility
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(0.4f), Color.Transparent)
                                )
                            )
                    )

                    // Pager Indicators
                    if (totalItems > 1) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 32.dp),
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(totalItems) { i ->
                                    val active = pagerState.currentPage == i
                                    Box(
                                        modifier = Modifier
                                            .size(if (active) 8.dp else 6.dp)
                                            .clip(CircleShape)
                                            .background(if (active) Color.White else Color.White.copy(0.5f))
                                    )
                                }
                            }
                        }
                    }
                }

                // Main Content
                Column(
                    modifier = Modifier
                        .offset(y = (-24).dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(top = 24.dp)
                ) {
                    // Price and Actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${product.price} ₸",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = onToggleLike,
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (liked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )

                    // Rating and Stats
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(20.dp))
                        Text(" ${formatRating(product.rating)} ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        VerticalDivider(modifier = Modifier.height(16.dp).padding(horizontal = 8.dp))
                        Text(
                            text = "${product.reviewsCount} отзывов",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { onOpenReviews(product.id) }
                        )
                        VerticalDivider(modifier = Modifier.height(16.dp).padding(horizontal = 8.dp))
                        Text(
                            "${product.ordersCount} заказов",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(44.dp)
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
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        if (tab == DetailsTab.DESCRIPTION) {
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 24.sp
                            )
                        } else {
                            SpecsBlock(product.specs)
                        }
                    }

                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

                    InfoSection(title = "Доставка", icon = Icons.Outlined.LocalShipping) {
                        DeliveryBlock(product.delivery)
                    }

                    InfoSection(title = "Продавец", icon = Icons.Outlined.Storefront) {
                        SellerBlock(product.seller, onClick = { onOpenSeller(product.seller.id) })
                    }

                    Spacer(Modifier.height(120.dp))
                }
            }

            // Top AppBar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .alpha(topBarAlpha)
                    .background(MaterialTheme.colorScheme.surface)
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
                        containerColor = if (topBarAlpha < 0.5f) Color.Transparent else Color.Transparent,
                        contentColor = if (topBarAlpha < 0.5f) Color.White else MaterialTheme.colorScheme.onSurface
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
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp).weight(1f)
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
fun YoutubeVideoSlide(videoId: String) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isReady by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                YouTubePlayerView(context).apply {
                    enableAutomaticInitialization = false
                    lifecycleOwner.lifecycle.addObserver(this)
                    val options = IFramePlayerOptions.Builder(context)
                        .controls(1).fullscreen(0).rel(0).ivLoadPolicy(3).build()
                    initialize(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                            isReady = true
                            hasError = false
                            youTubePlayer.loadVideo(videoId, 0f)
                        }
                        override fun onError(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer, error: PlayerConstants.PlayerError) {
                            hasError = true
                        }
                    }, options)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (!isReady && !hasError) CircularProgressIndicator(color = Color.White)
    }
}

private fun extractYoutubeVideoId(url: String?): String? {
    if (url.isNullOrBlank()) return null
    val regexes = listOf("(?<=v=)[a-zA-Z0-9_-]{11}", "(?<=youtu.be/)[a-zA-Z0-9_-]{11}", "(?<=/embed/)[a-zA-Z0-9_-]{11}", "(?<=/shorts/)[a-zA-Z0-9_-]{11}")
    for (pattern in regexes) {
        val match = Regex(pattern).find(url)?.value
        if (!match.isNullOrBlank()) return match
    }
    return null
}

@Composable
private fun InfoSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun BottomActionsBar(onChat: () -> Unit, onOrder: () -> Unit) {
    Surface(
        modifier = Modifier.shadow(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onChat,
                modifier = Modifier.weight(0.35f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Чат", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onOrder,
                modifier = Modifier.weight(0.65f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Купить сейчас", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SpecsBlock(specs: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        specs.forEach { (k, v) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = k, modifier = Modifier.weight(1.1f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                Text(text = v, modifier = Modifier.weight(0.9f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun DeliveryBlock(delivery: DeliveryInfoUi) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (delivery.freeDeliveryEnabled) {
                DeliveryRow(icon = Icons.Default.CheckCircle, label = "Доставка курьером", value = delivery.freeDeliveryText ?: "Бесплатно", isFree = true)
            }
            if (delivery.pickupEnabled) {
                DeliveryRow(icon = Icons.Default.LocationOn, label = "Самовывоз: ${delivery.pickupAddress}", value = "Бесплатно", isFree = true)
            }
            if (delivery.intercityEnabled) {
                DeliveryRow(icon = Icons.Default.Public, label = "Межгород", value = "Доступно", isFree = false)
            }
        }
    }
}

@Composable
private fun DeliveryRow(icon: ImageVector, label: String, value: String, isFree: Boolean) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isFree) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(10.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, fontWeight = FontWeight.ExtraBold, color = if (isFree) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SellerBlock(seller: SellerUi, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(52.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                if (!seller.avatarUrl.isNullOrBlank()) {
                    AsyncImage(model = seller.avatarUrl, contentDescription = null, contentScale = ContentScale.Crop)
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(seller.name.take(1).uppercase(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(text = seller.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = seller.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

private fun formatRating(rating: Double): String = if (rating == 0.0) "Новый" else "%.1f".format(rating)
