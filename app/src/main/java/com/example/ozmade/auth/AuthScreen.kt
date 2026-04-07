package com.example.ozmade.auth

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthNavHost(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit,
    onOpenPrivacy: () -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val activity = LocalContext.current as Activity

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onAuthSuccess()
    }

    when (val state = uiState) {
        is AuthUiState.OtpRequired -> OtpCodeScreen(
            phone = state.phone,
            isLoading = false,
            errorText = null,
            onVerify = { otp -> viewModel.verifyOtp(state.verificationId, otp, state.phone) },
            onResend = { viewModel.requestOtp(activity, state.phone) },
            onBackClick = { viewModel.reset() }
        )

        is AuthUiState.Loading -> PhoneLoginScreen(
            isLoading = true,
            errorText = null,
            onSendCode = { phone -> viewModel.requestOtp(activity, phone) },
            onBackClick = onBack,
            onOpenPrivacy = onOpenPrivacy,
            onOpenTerms = onOpenTerms
        )

        is AuthUiState.Error -> {
            if (state.phone != null && state.verificationId != null) {
                OtpCodeScreen(
                    phone = state.phone,
                    isLoading = false,
                    errorText = state.message,
                    onVerify = { otp -> viewModel.verifyOtp(state.verificationId, otp, state.phone) },
                    onResend = { viewModel.requestOtp(activity, state.phone) },
                    onBackClick = { viewModel.reset() }
                )
            } else {
                PhoneLoginScreen(
                    isLoading = false,
                    errorText = state.message,
                    onSendCode = { phone -> viewModel.requestOtp(activity, phone) },
                    onBackClick = onBack,
                    onOpenPrivacy = onOpenPrivacy,
                    onOpenTerms = onOpenTerms
                )
            }
        }

        else -> PhoneLoginScreen(
            isLoading = false,
            errorText = null,
            onSendCode = { phone -> viewModel.requestOtp(activity, phone) },
            onBackClick = onBack,
            onOpenPrivacy = onOpenPrivacy,
            onOpenTerms = onOpenTerms
        )
    }
}
