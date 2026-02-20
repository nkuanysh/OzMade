package com.example.ozmade.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.ozmade.auth.AuthNavHost
import com.example.ozmade.auth.LanguageScreen
import com.example.ozmade.main.MainScreen
import com.example.ozmade.main.locale.AppLang
import com.example.ozmade.main.locale.LanguageStore
import kotlinx.coroutines.launch

private object Routes {
    const val LANG = "lang"
    const val AUTH = "auth"
    const val HOME = "home"
}

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val langStore = remember { LanguageStore(context) }
    val scope = rememberCoroutineScope()

    // определяем стартовый экран
    var start by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // если язык уже выбирали — сразу auth
        start = if (langStore.isLangChosen()) Routes.AUTH else Routes.LANG
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
                onOpenPrivacy = { /* TODO открыть экран/веб */ },
                onOpenTerms = { /* TODO открыть экран/веб */ }
            )
        }

        composable(Routes.HOME) {
            MainScreen(
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}