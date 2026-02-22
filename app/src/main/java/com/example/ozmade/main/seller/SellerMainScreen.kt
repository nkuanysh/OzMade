package com.example.ozmade.main.seller

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.Alignment
import com.example.ozmade.main.seller.products.SellerProductsRoute

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

@Composable
fun SellerMainScreen(
    onExitSeller: () -> Unit = {}
) {
    val navController = rememberNavController()
    val items = listOf(
        SellerBottomItem.Products,
        SellerBottomItem.Orders,
        SellerBottomItem.Chat,
        SellerBottomItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = SellerBottomItem.Products.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(SellerBottomItem.Products.route) {
                SellerProductsRoute(
                    onAddProduct = { /* TODO nav to create */ },
                    onOpenEdit = { productId -> /* TODO nav to edit */ }
                )
            }
            composable(SellerBottomItem.Orders.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Seller Orders (TODO)")
                }
            }
            composable(SellerBottomItem.Chat.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Seller Chat (TODO)")
                }
            }
            composable(SellerBottomItem.Profile.route) {
                SellerProfileScreen(onBecomeBuyer = onExitSeller)
            }
        }
    }
}