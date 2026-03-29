package com.example.ozmade.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.ozmade.auth.AuthNavHost
import com.example.ozmade.auth.LanguageScreen
import com.example.ozmade.main.MainScreen
import com.example.ozmade.main.user.profile.locale.AppLang
import com.example.ozmade.main.user.profile.locale.LanguageStore
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

private object Routes {
    const val LANG = "lang"
    const val AUTH = "auth"
    const val HOME = "home"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
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
    val langStore = remember { LanguageStore(context) }
    val scope = rememberCoroutineScope()

    var start by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val langChosen = langStore.isLangChosen()
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        start = when {
            !langChosen -> Routes.LANG
            isLoggedIn -> Routes.HOME
            else -> Routes.AUTH
        }
    }

    if (start == null) return

    NavHost(navController = navController, startDestination = start!!) {

        composable(Routes.LANG) {
            LanguageScreen(
                onChooseKazakh = {
                    scope.launch {
                        langStore.setLang(AppLang.KK)
                        navController.navigate(Routes.AUTH) {
                            popUpTo(Routes.LANG) { inclusive = true }
                        }
                    }
                },
                onChooseRussian = {
                    scope.launch {
                        langStore.setLang(AppLang.RU)
                        navController.navigate(Routes.AUTH) {
                            popUpTo(Routes.LANG) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.AUTH) {
            AuthNavHost(
                onAuthSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onOpenPrivacy = { },
                onOpenTerms = { }
            )
        }

        composable(Routes.HOME) {
            MainScreen(
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                openChatFromPush = openChatFromPush,
                pushChatId = pushChatId,
                pushSellerId = pushSellerId,
                pushProductId = pushProductId,
                pushSellerName = pushSellerName,
                pushProductTitle = pushProductTitle,
                pushPrice = pushPrice,
                deepLinkProductId = deepLinkProductId
            )
        }
    }
}
