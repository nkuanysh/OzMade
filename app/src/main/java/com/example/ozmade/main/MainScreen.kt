package com.example.ozmade.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
//import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ozmade.main.profile.EditProfileScreen
import com.example.ozmade.main.profile.ProfileScreen
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ozmade.main.category.CategoryRoute
import com.example.ozmade.main.home.details.ProductDetailsRoute
import com.example.ozmade.main.reviews.ReviewsRoute
import com.example.ozmade.main.seller.SellerRoute
import com.example.ozmade.main.seller.reviews.SellerReviewsRoute
import com.example.ozmade.main.support.SupportScreen
import com.example.ozmade.main.support.SupportChatScreen
import android.net.Uri


private sealed class BottomItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
) {
    object Home : BottomItem("home", "Главная", { Icon(Icons.Default.Home, null) })
    object Favorites : BottomItem("favorites", "Избранное", { Icon(Icons.Default.Favorite, null) })
    object Chat : BottomItem("chat", "Чат", { Icon(Icons.Default.Email, null) })
    object Profile : BottomItem("profile", "Профиль", { Icon(Icons.Default.Person, null) })
}

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    fun openProductFromDeep(productId: String) {
        navController.navigate("product/$productId") {
            // очищаем всё до Home, чтобы Back из details возвращал Home
            popUpTo(BottomItem.Home.route) { inclusive = false }
            launchSingleTop = true
        }
    }

    val items = listOf(
        BottomItem.Home,
        BottomItem.Favorites,
        BottomItem.Chat,
        BottomItem.Profile
    )

    Scaffold(
        bottomBar = {
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
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomItem.Home.route) {
                HomeScreen(
                    onOpenProduct = { productId ->
                        navController.navigate("product/$productId")
                    }
                    ,
                    onOpenCategory = { categoryId ->
                        navController.navigate("category/$categoryId")
                    }
                )
            }
            composable(
                route = "category/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("id") ?: return@composable

                CategoryRoute(
                    categoryId = categoryId,
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { pid -> navController.navigate("product/$pid") }
                )
            }


            composable(BottomItem.Favorites.route) { FavoritesScreen() }
            composable(BottomItem.Chat.route) {
                ChatScreen(
                    onOpenSupportChat = { navController.navigate("chat_support") },
                    onOpenThread = { sellerId, productId ->
                        // пока без имени/данных — передадим заглушки, позже возьмём из repo
                        navController.navigate("chat/$sellerId/$productId?sellerName=Продавец&productTitle=Товар&price=0")
                    }
                )
            }



            composable(BottomItem.Profile.route) {
                ProfileScreen(
                    onLogout = onLogout,
                    onEditProfile = { navController.navigate("edit_profile") },
                    onSupport = { navController.navigate("support") }

                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("support") {
                SupportScreen(
                    onClose = { navController.popBackStack() },
                    onOpenSupportChat = { navController.navigate("support_chat") }
                )
            }
            composable("support_chat") {
                SupportChatScreen(
                    onBack = { navController.popBackStack() }
                )
            }


            composable(
                route = "product/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable

                ProductDetailsRoute(
                    productId = id,
                    onBack = { navController.popBackStack(BottomItem.Home.route, false) },
                    onChat = { sellerId, sellerName, productId, productTitle, price ->
                        val encSellerName = Uri.encode(sellerName)
                        val encProductTitle = Uri.encode(productTitle)

                        navController.navigate(
                            "chat/$sellerId/$productId?sellerName=$encSellerName&productTitle=$encProductTitle&price=$price"
                        )
                    },

                    onOrder = { /* TODO: оформить заказ */ },
                    onOpenReviews = { pid: String ->
                        navController.navigate("reviews/$pid")
                    },
                    onOpenSeller = { sellerId: String ->
                        navController.navigate("seller/$sellerId")
                    }




                )
            }
            composable(
                route = "reviews/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: return@composable

                ReviewsRoute(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "seller/{sellerId}",
                arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sellerId = backStackEntry.arguments?.getString("sellerId") ?: return@composable

                SellerRoute(
                    sellerId = sellerId,
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { productId -> openProductFromDeep(productId) },
                    onOpenSellerReviews = { sid: String ->
                        navController.navigate("seller_reviews/$sid")
                    }

                )
            }
            composable(
                route = "seller_reviews/{sellerId}",
                arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sellerId = backStackEntry.arguments?.getString("sellerId") ?: return@composable

                SellerReviewsRoute(
                    sellerId = sellerId,
                    onBack = { navController.popBackStack() },
                    onOpenProduct = { productId -> openProductFromDeep(productId) },

                )
            }

            composable(
                route = "chat/{sellerId}/{productId}?sellerName={sellerName}&productTitle={productTitle}&price={price}",
                arguments = listOf(
                    navArgument("sellerId") { type = NavType.StringType },
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("sellerName") { type = NavType.StringType; defaultValue = "" },
                    navArgument("productTitle") { type = NavType.StringType; defaultValue = "" },
                    navArgument("price") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { backStackEntry ->
                val sellerId = backStackEntry.arguments?.getString("sellerId") ?: return@composable
                val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
                val sellerName = backStackEntry.arguments?.getString("sellerName") ?: ""
                val productTitle = backStackEntry.arguments?.getString("productTitle") ?: ""
                val price = backStackEntry.arguments?.getInt("price") ?: 0

                com.example.ozmade.main.chat.ChatThreadRoute(
                    sellerId = sellerId,
                    productId = productId,
                    sellerName = sellerName.ifBlank { "Продавец" },
                    productTitle = productTitle.ifBlank { "Товар" },
                    productPrice = price,
                    onBack = { navController.popBackStack() }
                )
            }




        }

    }
}
