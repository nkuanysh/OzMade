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
import com.example.ozmade.main.home.details.ProductDetailsRoute


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
                )
            }

            composable(BottomItem.Favorites.route) { FavoritesScreen() }
            composable(BottomItem.Chat.route) { ChatScreen() }

            composable(BottomItem.Profile.route) {
                ProfileScreen(
                    onLogout = onLogout,
                    onEditProfile = { navController.navigate("edit_profile") }
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
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
                    onBack = { navController.popBackStack() },
                    onChat = { /* TODO: открыть чат с продавцом */ },
                    onOrder = { /* TODO: оформить заказ */ }
                )
            }

        }

    }
}
