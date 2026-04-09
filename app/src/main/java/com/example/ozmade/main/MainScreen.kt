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
import androidx.compose.ui.graphics.Color
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
import com.example.ozmade.main.seller.onboarding.SellerGateRoute
import com.example.ozmade.main.seller.onboarding.SellerOnboardingScreen
import com.example.ozmade.main.seller.registration.SellerRegistrationRoute
import com.example.ozmade.main.user.chat.ChatScreen
import com.example.ozmade.main.user.chat.ChatThreadRoute
import com.example.ozmade.main.user.favorites.FavoritesRoute
import com.example.ozmade.main.user.profile.EditProfileScreen
import com.example.ozmade.main.user.profile.ProfileScreen
import com.example.ozmade.main.user.profile.about.AboutAppScreen
import com.example.ozmade.main.user.profile.notification.NotificationsScreen
import com.example.ozmade.main.user.profile.orders.OrdersHistoryScreen
import com.example.ozmade.main.user.profile.support.SupportChatScreen
import com.example.ozmade.main.user.profile.support.SupportScreen
import com.example.ozmade.main.userHome.HomeRoute
import com.example.ozmade.main.userHome.category.CategoryRoute
import com.example.ozmade.main.userHome.details.ProductDetailsRoute
import com.example.ozmade.main.userHome.reviews.ReviewsRoute
import com.example.ozmade.main.userHome.seller.SellerRoute
import com.example.ozmade.main.userHome.seller.reviews.SellerReviewsRoute
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
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

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

    val currentDestination = navBackStackEntry?.destination

    val hideBottomBarRoutes = listOf(
        "chat/{chatId}/{sellerId}/{productId}?sellerName={sellerName}&productTitle={productTitle}&price={price}",
        "chat_new/{sellerId}/{productId}?sellerName={sellerName}&productTitle={productTitle}&price={price}",
        "support_chat",
        "seller_gate",
        "seller_onboarding",
        "seller_registration",
        "seller_main",
        "delivery/{productId}/{qty}",
        "buyer_orders",
        "buyer_order/{orderId}",
        "notifications",
        "orders_history",
        "about_app",
    )

    val showBottomBar = currentDestination?.route !in hideBottomBarRoutes &&
            currentDestination?.route?.startsWith("chat/") == false &&
            currentDestination?.route?.startsWith("chat_new/") == false &&
            currentDestination?.route != "seller_main"

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                CustomNavigationBar(navController, currentDestination)
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isSellerModePref == true) "seller_main" else BottomItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomItem.Home.route) {
                HomeRoute(
                    onOpenProduct = { id -> navController.navigate("product/$id") },
                    onOpenCategory = { id -> navController.navigate("category/$id") }
                )
            }

            composable(
                route = "category/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                CategoryRoute(
                    categoryId = backStackEntry.arguments?.getString("id") ?: "",
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { pid -> navController.navigate("product/$pid") }
                )
            }

            composable(
                route = "product/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                ProductDetailsRoute(
                    productId = backStackEntry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() },
                    onChat = { sid, sName, pid, pTitle, price ->
                        val encS = Uri.encode(sName)
                        val encT = Uri.encode(pTitle)
                        navController.navigate(
                            "chat_new/$sid/$pid?sellerName=$encS&productTitle=$encT&price=$price"
                        )
                    },
                    onOpenDelivery = { pid, qty ->
                        navController.navigate("delivery/$pid/$qty")
                    },
                    onOpenReviews = { pid -> navController.navigate("reviews/$pid") },
                    onOpenSeller = { sid -> navController.navigate("seller/$sid") }
                )
            }

            // Buyer Flow components from file 1
            composable(
                route = "delivery/{productId}/{qty}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.IntType },
                    navArgument("qty") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                val qty = backStackEntry.arguments?.getInt("qty") ?: 1

                com.example.ozmade.main.user.orderflow.ui.DeliveryChooseRoute2(
                    productId = productId,
                    quantity = qty,
                    onBack = { navController.popBackStack() },
                    onCreated = {
                        navController.navigate("buyer_orders") {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("buyer_orders") {
                com.example.ozmade.main.user.orders.BuyerOrdersRoute(
                    onBack = { navController.popBackStack() },
                    onOpenOrder = { orderId ->
                        navController.navigate("buyer_order/$orderId")
                    }
                )
            }

            composable(
                route = "buyer_order/{orderId}",
                arguments = listOf(
                    navArgument("orderId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0

                com.example.ozmade.main.user.orders.BuyerOrderDetailsRoute(
                    orderId = orderId,
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { pid ->
                        navController.navigate("product/$pid")
                    }
                )
            }

            composable(
                route = "reviews/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { b ->
                ReviewsRoute(
                    productId = b.arguments?.getInt("productId") ?: 0,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "seller_reviews/{sellerId}",
                arguments = listOf(navArgument("sellerId") { type = NavType.IntType })
            ) { b ->
                SellerReviewsRoute(
                    sellerId = b.arguments?.getInt("sellerId") ?: 0,
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { pid -> navController.navigate("product/$pid") }
                )
            }

            composable(BottomItem.Favorites.route) {
                FavoritesRoute(
                    onOpenProduct = { id -> navController.navigate("product/$id") },
                    onBuyClick = {
                        navController.navigate(BottomItem.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(BottomItem.Chat.route) {
                ChatScreen(
                    onOpenThread = { thread ->
                        val encS = Uri.encode(thread.sellerName)
                        val encT = Uri.encode(thread.productTitle)
                        navController.navigate(
                            "chat/${thread.chatId}/${thread.sellerId}/${thread.productId}?sellerName=$encS&productTitle=$encT&price=${thread.productPrice}"
                        )
                    },
                    onOpenSupportChat = { navController.navigate("support_chat") },
                    onNavigateToHome = {
                        navController.navigate(BottomItem.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(
                route = "chat/{chatId}/{sellerId}/{productId}?sellerName={sellerName}&productTitle={productTitle}&price={price}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.IntType },
                    navArgument("sellerId") { type = NavType.IntType },
                    navArgument("productId") { type = NavType.IntType },
                    navArgument("sellerName") { defaultValue = "" },
                    navArgument("productTitle") { defaultValue = "" },
                    navArgument("price") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { backStackEntry ->
                ChatThreadRoute(
                    chatId = backStackEntry.arguments?.getInt("chatId"),
                    sellerId = backStackEntry.arguments?.getInt("sellerId") ?: 0,
                    productId = backStackEntry.arguments?.getInt("productId") ?: 0,
                    sellerName = backStackEntry.arguments?.getString("sellerName") ?: "Продавец",
                    productTitle = backStackEntry.arguments?.getString("productTitle") ?: "Товар",
                    productPrice = backStackEntry.arguments?.getInt("price") ?: 0,
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { pid -> navController.navigate("product/$pid") }
                )
            }

            composable(
                route = "chat_new/{sellerId}/{productId}?sellerName={sellerName}&productTitle={productTitle}&price={price}",
                arguments = listOf(
                    navArgument("sellerId") { type = NavType.IntType },
                    navArgument("productId") { type = NavType.IntType },
                    navArgument("sellerName") { defaultValue = "" },
                    navArgument("productTitle") { defaultValue = "" },
                    navArgument("price") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { backStackEntry ->
                ChatThreadRoute(
                    chatId = null,
                    sellerId = backStackEntry.arguments?.getInt("sellerId") ?: 0,
                    productId = backStackEntry.arguments?.getInt("productId") ?: 0,
                    sellerName = backStackEntry.arguments?.getString("sellerName") ?: "Продавец",
                    productTitle = backStackEntry.arguments?.getString("productTitle") ?: "Товар",
                    productPrice = backStackEntry.arguments?.getInt("price") ?: 0,
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { pid -> navController.navigate("product/$pid") }
                )
            }

            composable(BottomItem.Profile.route) {
                ProfileScreen(
                    onLogout = onLogout,
                    onEditProfile = { navController.navigate("edit_profile") },
                    onNotifications = { navController.navigate("notifications") },
                    onOrderHistory = { navController.navigate("orders_history") },
                    onSupport = { navController.navigate("support") },
                    onAbout = { navController.navigate("about_app") },
                    onBecomeSeller = { navController.navigate("seller_gate") }
                )
            }

            composable("edit_profile") { EditProfileScreen(onBack = { navController.popBackStack() }) }
            composable("notifications") { NotificationsScreen(onBack = { navController.popBackStack() }) }
            composable("orders_history") {
                com.example.ozmade.main.user.orders.BuyerOrdersRoute(
                    onBack = { navController.popBackStack() },
                    onOpenOrder = { orderId ->
                        navController.navigate("buyer_order/$orderId")
                    }
                )
            }
            composable("about_app") { AboutAppScreen(onBack = { navController.popBackStack() }) }
            composable("support") {
                SupportScreen(onClose = { navController.popBackStack() }, onOpenSupportChat = { navController.navigate("support_chat") })
            }
            composable("support_chat") { SupportChatScreen(onBack = { navController.popBackStack() }) }

            composable(
                route = "seller/{sellerId}",
                arguments = listOf(navArgument("sellerId") { type = NavType.IntType })
            ) { b ->
                SellerRoute(
                    sellerId = b.arguments?.getInt("sellerId") ?: 0,
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { pid -> navController.navigate("product/$pid") },
                    onOpenSellerReviews = { sid -> navController.navigate("seller_reviews/$sid") }
                )
            }

            composable("seller_gate") {
                SellerGateRoute(
                    onOpenSellerHome = {
                        scope.launch {
                            sellerStore.setSellerMode(true)
                            navController.navigate("seller_main") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onOpenOnboarding = {
                        navController.navigate("seller_onboarding") {
                            popUpTo("seller_gate") { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("seller_onboarding") {
                SellerOnboardingScreen(
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate("seller_registration") }
                )
            }

            composable("seller_registration") {
                SellerRegistrationRoute(
                    onBack = { navController.popBackStack() },
                    onOpenSellerTerms = {},
                    onOpenPrivacy = {},
                    onSuccess = {
                        scope.launch {
                            sellerStore.setSellerMode(true)
                            navController.navigate("seller_main") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("seller_main") {
                SellerMainScreen(
                    onExitSeller = {
                        scope.launch {
                            sellerStore.setSellerMode(false)
                            navController.navigate(BottomItem.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomNavigationBar(
    navController: androidx.navigation.NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    val items = listOf(BottomItem.Home, BottomItem.Favorites, BottomItem.Chat, BottomItem.Profile)

    NavigationBar(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = isSelected,
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else null
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF9800),
                    selectedTextColor = Color(0xFFFF9800),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFFFF3E0)
                )
            )
        }
    }
}