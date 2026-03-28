package com.example.ozmade.main

import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ozmade.main.seller.SellerMainScreen
import com.example.ozmade.main.seller.onboarding.SellerGateRoute
import com.example.ozmade.main.seller.onboarding.SellerOnboardingScreen
import com.example.ozmade.main.user.chat.ChatScreen
import com.example.ozmade.main.user.chat.ChatThreadRoute
import com.example.ozmade.main.user.favorites.FavoritesRoute
import com.example.ozmade.main.user.favorites.FavoritesScreen
import com.example.ozmade.main.user.profile.EditProfileScreen
import com.example.ozmade.main.user.profile.ProfileScreen
import com.example.ozmade.main.user.profile.support.SupportChatScreen
import com.example.ozmade.main.user.profile.support.SupportScreen
import com.example.ozmade.main.userHome.HomeScreen
import com.example.ozmade.main.userHome.category.CategoryRoute
import com.example.ozmade.main.userHome.details.ProductDetailsRoute
import com.example.ozmade.main.userHome.reviews.ReviewsRoute
import com.example.ozmade.main.userHome.seller.SellerRoute
import com.example.ozmade.main.userHome.seller.reviews.SellerReviewsRoute

// 1. Красивая структура для элементов меню
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
    pushPrice: Int = 0
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(openChatFromPush, pushChatId) {
        if (openChatFromPush && pushChatId != 0) {

            val encSellerName = Uri.encode(pushSellerName)
            val encProductTitle = Uri.encode(pushProductTitle)

            navController.navigate(
                "chat/$pushChatId/$pushSellerId/$pushProductId?sellerName=$encSellerName&productTitle=$encProductTitle&price=$pushPrice"

            )
            Log.d("PUSH", "OPEN CHAT: $pushChatId")
        }
    }

    val currentDestination = navBackStackEntry?.destination

    // Список путей, где нужно СКРЫТЬ нижний бар
    val hideBottomBarRoutes = listOf(
        "chat/{sellerId}/{productId}",
        "support_chat",
        "seller_gate",
        "seller_onboarding",
        "seller_registration",
        "seller_main",
        "delivery/{productId}/{qty}",
        "buyer_orders",
        "buyer_order/{orderId}",
    )

    val showBottomBar = currentDestination?.route !in hideBottomBarRoutes &&
            currentDestination?.route?.startsWith("chat/") == false

    // Функция для глубокой навигации
    fun openProductFromDeep(productId: Int) {
        navController.navigate("product/$productId") {
            popUpTo(BottomItem.Home.route) { inclusive = false }
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            // Плавная анимация появления/исчезновения бара
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                CustomNavigationBar(navController, currentDestination)
            }
        },
        containerColor = Color.White // Чистый фон для всего приложения
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            // --- ГЛАВНАЯ И ТОВАРЫ ---
            composable(BottomItem.Home.route) {
                HomeScreen(
                    onOpenProduct = { id -> navController.navigate("product/$id") },
                    onOpenCategory = { id -> navController.navigate("category/$id") }
                )
            }

            composable(
                route = "category/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
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
            //ORDER
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



            // --- ИЗБРАННОЕ ---
            composable(BottomItem.Favorites.route) { FavoritesRoute(
                onBuyClick = {
                    navController.navigate("home")
                },
                onOpenProduct = { id -> navController.navigate("product/$id")
                }
            ) }

            // --- ЧАТЫ ---
            composable(BottomItem.Chat.route) {
                ChatScreen(
                    onOpenThread = { t ->
                        val encS = Uri.encode(t.sellerName)
                        val encT = Uri.encode(t.productTitle)
                        navController.navigate(
                            "chat/${t.chatId}/${t.sellerId}/${t.productId}?sellerName=$encS&productTitle=$encT&price=${t.productPrice}"
                        )
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
                        onOpenProduct = { pid -> openProductFromDeep(pid) }
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
                    onOpenProduct = { pid -> openProductFromDeep(pid) }
                )
            }

            // --- ПРОФИЛЬ И ПОДДЕРЖКА ---
            composable(BottomItem.Profile.route) {
                ProfileScreen(
                    onLogout = onLogout,
                    onEditProfile = { navController.navigate("edit_profile") },
                    onSupport = { navController.navigate("support") },
                    onBecomeSeller = { navController.navigate("seller_gate") }
                )
            }

            composable("edit_profile") { EditProfileScreen(onBack = { navController.popBackStack() }) }
            composable("support") {
                SupportScreen(onClose = { navController.popBackStack() }, onOpenSupportChat = { navController.navigate("support_chat") })
            }
            composable("support_chat") { SupportChatScreen(onBack = { navController.popBackStack() }) }

            // --- ПРОДАВЕЦ ---
            composable("seller/{sellerId}") { b ->
                SellerRoute(
                    sellerId = b.arguments?.getInt("sellerId") ?: 0,
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { pid -> openProductFromDeep(pid) },
                    onOpenSellerReviews = { sid -> navController.navigate("seller_reviews/$sid") }
                )
            }

            composable("seller_gate") {
                SellerGateRoute(
                    onOpenSellerHome = { navController.navigate("seller_main") },
                    onOpenOnboarding = { navController.navigate("seller_onboarding") },
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
                com.example.ozmade.main.seller.registration.SellerRegistrationRoute(
                    onBack = { navController.popBackStack() },
                    onOpenSellerTerms = {},
                    onOpenPrivacy = {},
                    onSuccess = {
                        navController.navigate("seller_main") { popUpTo("seller_registration") { inclusive = true } }
                    }
                )
            }

            composable("seller_main") {
                SellerMainScreen(onExitSeller = {
                    navController.navigate(BottomItem.Profile.route) { launchSingleTop = true }
                })
            }

            // Доп. экраны
            composable("reviews/{productId}") { b ->
                ReviewsRoute(productId = b.arguments?.getInt("productId") ?: 0, onBack = { navController.popBackStack() })
            }
            composable("seller_reviews/{sellerId}") { b ->
                SellerReviewsRoute(sellerId = b.arguments?.getInt("sellerId") ?: 0, onBack = { navController.popBackStack() }, onOpenProduct = { pid -> openProductFromDeep(pid) })
            }
        }
    }
}

// Отдельный UI компонент для нижнего бара, чтобы не перегружать MainScreen
@Composable
private fun CustomNavigationBar(
    navController: androidx.navigation.NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    val items = listOf(BottomItem.Home, BottomItem.Favorites, BottomItem.Chat, BottomItem.Profile)

    NavigationBar(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)), // Скругленные углы
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = isSelected,
                label = { Text(item.label, fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else null) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
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
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    }
}