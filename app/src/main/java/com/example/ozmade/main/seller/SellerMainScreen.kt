package com.example.ozmade.main.seller

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.ozmade.main.seller.products.SellerProductsRoute
import com.example.ozmade.main.seller.profile.SellerStoreSettingsScreen

private sealed class SellerBottomItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
) {
    object Products : SellerBottomItem("seller_products_tab", "Товары", { Icon(Icons.Default.CheckCircle, null) })
    object Orders : SellerBottomItem("seller_orders_tab", "Заказы", { Icon(Icons.Default.ShoppingCart, null) })
    object Chat : SellerBottomItem("seller_chat_tab", "Чат", { Icon(Icons.Default.Email, null) })
    object Profile : SellerBottomItem("seller_profile_tab", "Профиль", { Icon(Icons.Default.Person, null) })
}

private object SellerRoutes {
    const val ADD_PRODUCT = "seller_add_product"
    const val EDIT_PRODUCT = "seller_edit_product"
    const val CHAT_THREAD = "seller_chat_thread"
    const val QUALITY = "seller_quality"
    const val DELIVERY = "seller_delivery"
    const val STORE_SETTINGS = "seller_store_settings"
}

@Composable
fun SellerMainScreen(
    onLogout: () -> Unit = {},
    onExitSeller: () -> Unit = {}
) {
    val navController = rememberNavController()
    val items = listOf(
        SellerBottomItem.Products,
        SellerBottomItem.Orders,
        SellerBottomItem.Chat,
        SellerBottomItem.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = when (currentRoute) {
        SellerRoutes.ADD_PRODUCT -> false
        SellerRoutes.EDIT_PRODUCT -> false
        SellerRoutes.STORE_SETTINGS -> false
        else -> true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

                    items.forEach { item ->
                        val selected = currentDestination?.route == item.route

                        NavigationBarItem(
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
                            icon = item.icon,
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = SellerBottomItem.Products.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(SellerBottomItem.Products.route) {
                SellerProductsRoute(
                    onAddProduct = { navController.navigate(SellerRoutes.ADD_PRODUCT) },
                    onOpenEdit = { productId ->
                        navController.navigate("${SellerRoutes.EDIT_PRODUCT}/$productId")
                    }
                )
            }
            composable(SellerRoutes.ADD_PRODUCT) {
                com.example.ozmade.main.seller.products.add.SellerAddProductRoute(
                    onBack = { navController.popBackStack() },
                    onCreated = {
                        navController.popBackStack()
                    }
                )
            }
            composable(SellerBottomItem.Orders.route) {
                com.example.ozmade.main.seller.orders.SellerOrdersRoute(
                    onOpenOrder = { id -> navController.navigate("seller_order_details/$id") }
                )
            }

            composable("seller_order_details/{id}") { back ->
                val id = back.arguments?.getString("id")?.toIntOrNull() ?: return@composable
                com.example.ozmade.main.seller.orders.SellerOrderDetailsRoute(
                    orderId = id,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(SellerBottomItem.Chat.route) {
                com.example.ozmade.main.seller.chat.SellerChatScreen(
                    onOpenChat = { thread ->
                        navController.navigate("seller_chat_thread/${thread.chatId}/${thread.buyerName}")
                    }
                )
            }
            composable("${SellerRoutes.CHAT_THREAD}/{chatId}/{buyerName}") { backStack ->
                val chatId = backStack.arguments?.getString("chatId")?.toIntOrNull() ?: return@composable
                val buyerName = backStack.arguments?.getString("buyerName") ?: "Покупатель"

                com.example.ozmade.main.seller.chat.SellerChatThreadRoute(
                    chatId = chatId,
                    buyerName = buyerName,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(SellerBottomItem.Profile.route) {
                SellerProfileScreen(
                    onLogout = onLogout,
                    onBecomeBuyer = onExitSeller,
                    onOpenProducts = {
                        navController.navigate(SellerBottomItem.Products.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenQuality = {
                        navController.navigate(SellerRoutes.QUALITY)
                    },
                    onOpenOrders = {
                        navController.navigate(SellerBottomItem.Orders.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenDelivery = {
                        navController.navigate(SellerRoutes.DELIVERY)
                    },
                    onOpenStoreSettings = {
                        navController.navigate(SellerRoutes.STORE_SETTINGS)
                    }
                )
            }
            composable(SellerRoutes.QUALITY) {
                com.example.ozmade.main.seller.quality.SellerQualityRoute(
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { productId ->
                        // navigate to product details if needed
                    }
                )
            }
            composable(SellerRoutes.DELIVERY) {
                com.example.ozmade.main.seller.delivery.SellerDeliveryRoute(
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(SellerBottomItem.Products.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(SellerRoutes.STORE_SETTINGS) {
                SellerStoreSettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("${SellerRoutes.EDIT_PRODUCT}/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable

                com.example.ozmade.main.seller.products.edit.SellerEditProductRoute(
                    productId = id,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
