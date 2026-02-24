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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = when (currentRoute) {
        SellerRoutes.ADD_PRODUCT -> false
        SellerRoutes.EDIT_PRODUCT -> false

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
            modifier = Modifier.padding(padding) // ✅ как в user части
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
                        // после успешного создания вернёмся на список и обновим
                        navController.popBackStack()
                    }
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
