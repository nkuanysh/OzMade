package com.example.ozmade.main.seller.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.seller.data.SellerLocalStore
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun SellerGateRoute(
    onOpenSellerHome: () -> Unit,
    onOpenOnboarding: () -> Unit,
    onBack: () -> Unit,
    viewModel: SellerGateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val sellerStore = remember { SellerLocalStore(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.checkAndRoute() }

    LaunchedEffect(state) {
        when (state) {
            SellerGateState.OpenSellerHome -> {
                viewModel.reset()
                scope.launch {
                    sellerStore.setSellerMode(true)
                    onOpenSellerHome()
                }
            }
            SellerGateState.OpenOnboarding -> {
                viewModel.reset()
                onOpenOnboarding()
            }
            else -> Unit
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val s = state) {
            SellerGateState.Loading -> CircularProgressIndicator()
            is SellerGateState.Error -> Text(s.message)
            else -> Unit
        }
    }

}
