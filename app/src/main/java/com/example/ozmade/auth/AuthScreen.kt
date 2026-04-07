package com.example.ozmade.auth

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current

    // Launcher for notification permission
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // We just requested it, now proceed to success callback
        onAuthSuccess()
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = Manifest.permission.POST_NOTIFICATIONS
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    onAuthSuccess()
                } else {
                    launcher.launch(permission)
                }
            } else {
                onAuthSuccess()
            }
        }
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
