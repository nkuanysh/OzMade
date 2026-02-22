package com.example.ozmade.main.seller.registration

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SellerRegistrationRoute(
    onBack: () -> Unit,
    onOpenSellerTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: SellerRegistrationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is SellerRegState.Success) {
            viewModel.reset()
            onSuccess()
        }
    }

    SellerRegistrationScreen(
        onBack = onBack,
        onOpenSellerTerms = onOpenSellerTerms,
        onOpenPrivacy = onOpenPrivacy,
        onSubmit = { req -> viewModel.submit(req) },
        isLoading = state is SellerRegState.Loading,
        errorText = (state as? SellerRegState.Error)?.message
    )
}