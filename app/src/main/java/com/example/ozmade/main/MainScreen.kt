package com.example.ozmade.main

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ozmade.main.seller.SellerMainScreen
import com.example.ozmade.main.seller.data.SellerLocalStore
import com.example.ozmade.main.seller.registration.SellerRegistrationRoute
import com.example.ozmade.main.user.chat.ChatScreen
import com.example.ozmade.main.user.chat.ChatThreadRoute
import com.example.ozmade.main.user.favorites.FavoritesRoute
import com.example.ozmade.main.user.profile.EditProfileScreen
import com.example.ozmade.main.user.profile.ProfileScreen
import com.example.ozmade.main.user.profile.about.AboutAppScreen
import com.example.ozmade.main.user.profile.notification.NotificationsScreen
import com.example.ozmade.main.user.orders.BuyerOrdersRoute
import com.example.ozmade.main.user.profile.support.SupportChatScreen
import com.example.ozmade.main.user.profile.support.SupportScreen
import com.example.ozmade.main.userHome.HomeRoute
import com.example.ozmade.main.userHome.category.CategoryRoute
import com.example.ozmade.main.userHome.details.ProductDetailsRoute
import com.example.ozmade.main.userHome.reviews.ReviewsRoute
import com.example.ozmade.main.userHome.seller.SellerRoute
import com.example.ozmade.main.userHome.seller.reviews.SellerReviewsRoute
import com.example.ozmade.main.user.orderflow.ui.DeliveryChooseRoute2
import kotlinx.coroutines.launch

private sealed class BottomItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomItem("home", "Главная", Icons.Filled.Home, Icons.Outlined.Home)
    object Favorites : BottomItem("favorites", "Избранное", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object Chat : BottomItem("chat", "Чаты", Icons.Filled.Email, Icons.Outlined.Email)
    object Profile : BottomItem("profile", "Профиль", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    openChatFromPush: Boolean = false,
    pushChatId: Int = 0,
    pushSellerId: Int = 0,
    pushProductId: Int = 0,
    pushSellerName: String = "Продавец",
    pushProductTitle: String = "Товар",
    pushPrice: Int = 0,
    deepLinkProductId: Int = 0
) {
    val context = LocalContext.current
    val sellerStore = remember { SellerLocalStore(context) }
    val isSellerModePref by sellerStore.isSellerModeFlow.collectAsState(initial = null)
    var isRegistered by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(Unit) {
        isRegistered = sellerStore.isSellerRegistered()
    }

    // Wait until preference is loaded to avoid flickering
    if (isSellerModePref == null) return

    LaunchedEffect(openChatFromPush, pushChatId) {
        if (openChatFromPush && pushChatId != 0) {
            val encSellerName = Uri.encode(pushSellerName)
            val encProductTitle = Uri.encode(pushProductTitle)

            navController.navigate(
                "chat/$pushChatId/$pushSellerId/$pushProductId?sellerName=$encSellerName&productTitle=$encProductTitle&price=$pushPrice"
            )
        }
    }

    LaunchedEffect(deepLinkProductId) {
        if (deepLinkProductId != 0) {
            navController.navigate("product/$deepLinkProductId") {
                popUpTo(BottomItem.Home.route) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    if (isSellerModePref == true) {
        SellerMainScreen(
            onLogout = onLogout,
            onExitSeller = {
                scope.launch { sellerStore.setSellerMode(false) }
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                val currentDestination = navBackStackEntry?.destination
                val showBottomBar = listOf(
                    BottomItem.Home.route,
                    BottomItem.Favorites.route,
                    BottomItem.Chat.route,
                    BottomItem.Profile.route
                ).any { route ->
                    currentDestination?.hierarchy?.any { it.route == route } == true
                }

                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    ) {
                        val items = listOf(
                            BottomItem.Home,
                            BottomItem.Favorites,
                            BottomItem.Chat,
                            BottomItem.Profile
                        )
                        items.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = BottomItem.Home.route,
                Modifier.padding(innerPadding)
            ) {
                composable(BottomItem.Home.route) {
                    HomeRoute(
                        onOpenProduct = { pid -> navController.navigate("product/$pid") },
                        onOpenCategory = { catId -> 
                            navController.navigate("category/0?title=$catId") 
                        }
                    )
                }
                composable(BottomItem.Favorites.route) {
                    FavoritesRoute(
                        onOpenProduct = { pid -> navController.navigate("product/$pid") },
                        onBuyClick = {
                            navController.navigate(BottomItem.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(BottomItem.Chat.route) {
                    ChatScreen(
                        onOpenSupportChat = { navController.navigate("support") },
                        onOpenThread = { t ->
                            val encName = Uri.encode(t.sellerName)
                            val encTitle = Uri.encode(t.productTitle)
                            navController.navigate("chat/${t.chatId}/${t.sellerId}/${t.productId}?sellerName=$encName&productTitle=$encTitle&price=${t.productPrice}")
                        },
                        onNavigateToHome = {
                            navController.navigate(BottomItem.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(BottomItem.Profile.route) {
                    ProfileScreen(
                        onLogout = onLogout,
                        onEditProfile = { navController.navigate("edit_profile") },
                        onNotifications = { navController.navigate("notifications") },
                        onOrderHistory = { navController.navigate("orders_history") },
                        onSupport = { navController.navigate("support") },
                        onAbout = { navController.navigate("about") },
                        onBecomeSeller = {
                            if (isRegistered) {
                                scope.launch { sellerStore.setSellerMode(true) }
                            } else {
                                navController.navigate("seller_registration")
                            }
                        }
                    )
                }

                composable("seller_registration") {
                    SellerRegistrationRoute(
                        onBack = { navController.popBackStack() },
                        onOpenSellerTerms = { },
                        onOpenPrivacy = { },
                        onSuccess = {
                            scope.launch {
                                sellerStore.setSellerRegistered(true)
                                isRegistered = true
                                sellerStore.setSellerMode(true)
                            }
                        }
                    )
                }

                composable("edit_profile") {
                    EditProfileScreen(onBack = { navController.popBackStack() })
                }
                composable("orders_history") {
                    BuyerOrdersRoute(
                        onBack = { navController.popBackStack() },
                        onOpenOrder = { /* TODO */ }
                    )
                }
                composable("support") {
                    SupportScreen(
                        onClose = { navController.popBackStack() },
                        onOpenSupportChat = { navController.navigate("support_chat") }
                    )
                }
                composable("support_chat") {
                    SupportChatScreen(onBack = { navController.popBackStack() })
                }
                composable("about") {
                    AboutAppScreen(onBack = { navController.popBackStack() })
                }
                composable("notifications") {
                    NotificationsScreen(onBack = { navController.popBackStack() })
                }

                composable(
                    "category/{categoryId}?title={title}",
                    arguments = listOf(
                        navArgument("categoryId") { type = NavType.IntType },
                        navArgument("title") { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
                    val title = backStackEntry.arguments?.getString("title") ?: ""
                    
                    CategoryRoute(
                        categoryId = if (title.isNotEmpty()) title else categoryId.toString(),
                        onBack = { navController.popBackStack() },
                        onOpenProduct = { pid -> navController.navigate("product/$pid") }
                    )
                }

                composable(
                    "product/{productId}",
                    arguments = listOf(navArgument("productId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                    ProductDetailsRoute(
                        productId = productId,
                        onBack = { navController.popBackStack() },
                        onOpenSeller = { sid -> navController.navigate("seller/$sid") },
                        onChat = { sellerId, sellerName, prodId, productTitle, price ->
                            val encSName = Uri.encode(sellerName)
                            val encPTitle = Uri.encode(productTitle)
                            navController.navigate("chat/0/$sellerId/$prodId?sellerName=$encSName&productTitle=$encPTitle&price=${price.toInt()}")
                        },
                        onOpenDelivery = { pid, quantity -> 
                            navController.navigate("delivery_choose/$pid/$quantity")
                        },
                        onOpenReviews = { pid -> navController.navigate("reviews/$pid") }
                    )
                }

                composable(
                    "delivery_choose/{productId}/{quantity}",
                    arguments = listOf(
                        navArgument("productId") { type = NavType.IntType },
                        navArgument("quantity") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                    val quantity = backStackEntry.arguments?.getInt("quantity") ?: 1
                    DeliveryChooseRoute2(
                        productId = productId,
                        quantity = quantity,
                        onBack = { navController.popBackStack() },
                        onCreated = {
                            navController.navigate("orders_history") {
                                popUpTo("product/$productId") { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    "seller/{sellerId}",
                    arguments = listOf(navArgument("sellerId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val sellerId = backStackEntry.arguments?.getInt("sellerId") ?: 0
                    SellerRoute(
                        sellerId = sellerId,
                        onBack = { navController.popBackStack() },
                        onOpenProduct = { pid -> navController.navigate("product/$pid") },
                        onOpenSellerReviews = { sid -> navController.navigate("seller_reviews/$sid") }
                    )
                }

                composable(
                    "reviews/{productId}",
                    arguments = listOf(navArgument("productId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                    ReviewsRoute(
                        productId = productId,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    "seller_reviews/{sellerId}",
                    arguments = listOf(navArgument("sellerId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val sellerId = backStackEntry.arguments?.getInt("sellerId") ?: 0
                    SellerReviewsRoute(
                        sellerId = sellerId,
                        onBack = { navController.popBackStack() },
                        onOpenProduct = { pid -> navController.navigate("product/$pid") }
                    )
                }

                composable(
                    "chat/{chatId}/{sellerId}/{productId}?sellerName={sellerName}&productTitle={productTitle}&price={price}",
                    arguments = listOf(
                        navArgument("chatId") { type = NavType.IntType },
                        navArgument("sellerId") { type = NavType.IntType },
                        navArgument("productId") { type = NavType.IntType },
                        navArgument("sellerName") { type = NavType.StringType },
                        navArgument("productTitle") { type = NavType.StringType },
                        navArgument("price") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val chatId = backStackEntry.arguments?.getInt("chatId") ?: 0
                    val sellerId = backStackEntry.arguments?.getInt("sellerId") ?: 0
                    val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                    val sellerName = backStackEntry.arguments?.getString("sellerName") ?: ""
                    val productTitle = backStackEntry.arguments?.getString("productTitle") ?: ""
                    val price = backStackEntry.arguments?.getInt("price") ?: 0

                    ChatThreadRoute(
                        chatId = if (chatId == 0) null else chatId,
                        sellerId = sellerId,
                        productId = productId,
                        sellerName = sellerName,
                        productTitle = productTitle,
                        productPrice = price,
                        onBack = { navController.popBackStack() },
                        onOpenProduct = { pid -> navController.navigate("product/$pid") }
                    )
                }
            }
        }
    }
}
