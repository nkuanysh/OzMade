package com.example.ozmade.main.seller.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SellerGateRoute(
    onOpenSellerHome: () -> Unit,
    onOpenOnboarding: () -> Unit,
    onBack: () -> Unit,
    viewModel: SellerGateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.checkAndRoute() }

    LaunchedEffect(state) {
        when (state) {
            SellerGateState.OpenSellerHome -> {
                viewModel.reset()
                onOpenSellerHome()
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