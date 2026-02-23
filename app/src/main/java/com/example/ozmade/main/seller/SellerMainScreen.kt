package com.example.ozmade.main.seller

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ozmade.main.seller.products.SellerProductsRoute

private sealed class SellerBottomItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Products : SellerBottomItem(
        "seller_products_tab", "Товары",
        Icons.Filled.Inventory2, Icons.Outlined.Inventory2
    )
    object Orders : SellerBottomItem(
        "seller_orders_tab", "Заказы",
        Icons.Filled.Assignment, Icons.Outlined.Assignment
    )
    object Chat : SellerBottomItem(
        "seller_chat_tab", "Чат",
        Icons.Filled.QuestionAnswer, Icons.Outlined.QuestionAnswer
    )
    object Profile : SellerBottomItem(
        "seller_profile_tab", "Кабинет",
        Icons.Filled.Storefront, Icons.Outlined.Storefront
    )
}

@Composable
fun SellerMainScreen(
    onExitSeller: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        SellerBottomItem.Products,
        SellerBottomItem.Orders,
        SellerBottomItem.Chat,
        SellerBottomItem.Profile
    )

    Scaffold(
        containerColor = Color(0xFFFBFBFB), // Очень светлый серый для чистоты
        bottomBar = {
            NavigationBar(
                modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                containerColor = Color.White,
                tonalElevation = 10.dp
            ) {
                items.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        // Добавляем простую анимацию смены экранов
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = SellerBottomItem.Products.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                composable(SellerBottomItem.Products.route) {
                    SellerProductsRoute(
                        onAddProduct = { /* TODO: на экран создания */ },
                        onOpenEdit = { productId -> /* TODO: на экран редактирования */ }
                    )
                }
                composable(SellerBottomItem.Orders.route) {
                    PlaceholderScreen("У вас пока нет активных заказов", Icons.Outlined.ShoppingCart)
                }
                composable(SellerBottomItem.Chat.route) {
                    PlaceholderScreen("Сообщения от покупателей появятся здесь", Icons.Outlined.Email)
                }
                composable(SellerBottomItem.Profile.route) {
                    SellerProfileScreen(onBecomeBuyer = onExitSeller)
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(text: String, icon: ImageVector) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}