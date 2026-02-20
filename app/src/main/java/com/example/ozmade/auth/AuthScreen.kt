package com.example.ozmade.auth

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthNavHost(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit,
    onOpenPrivacy: () -> Unit = {},
    onOpenTerms: () -> Unit = {}
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
            errorText = (uiState as? AuthUiState.Error)?.message,
            onVerify = { otp -> viewModel.verifyOtp(state.verificationId, otp) },
            onResend = { viewModel.requestOtp(activity, state.phone) } // повторно отправить
        )

        is AuthUiState.Loading -> PhoneLoginScreen(
            isLoading = true,
            errorText = null,
            onSendCode = { phone -> viewModel.requestOtp(activity, phone) },
            onOpenPrivacy = onOpenPrivacy,
            onOpenTerms = onOpenTerms
        )

        is AuthUiState.Error -> PhoneLoginScreen(
            isLoading = false,
            errorText = state.message,
            onSendCode = { phone -> viewModel.requestOtp(activity, phone) },
            onOpenPrivacy = onOpenPrivacy,
            onOpenTerms = onOpenTerms
        )

        else -> PhoneLoginScreen(
            isLoading = false,
            errorText = null,
            onSendCode = { phone -> viewModel.requestOtp(activity, phone) },
            onOpenPrivacy = onOpenPrivacy,
            onOpenTerms = onOpenTerms
        )
    }
}

@Composable
fun OtpScreen(phone: String, onVerify: (String) -> Unit) {
    var otp by remember { mutableStateOf("") }

    Text("Enter OTP sent to $phone", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(value = otp, onValueChange = { otp = it }, label = { Text("OTP Code") })

    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = { onVerify(otp) }, modifier = Modifier.fillMaxWidth()) {
        Text("Verify & Register")
    }
}
@Composable
fun PhoneScreen(onRequestOtp: (String) -> Unit) {
    var phone by remember { mutableStateOf("") }

    Text("Введите номер", style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(16.dp))

    OutlinedTextField(
        value = phone,
        onValueChange = { phone = it },
        label = { Text("Номер телефона") },
        placeholder = { Text("+7XXXXXXXXXX") }
    )

    Spacer(Modifier.height(16.dp))
    Button(
        onClick = { onRequestOtp(phone.trim()) },
        modifier = Modifier.fillMaxWidth()
    ) { Text("Получить код") }
}
