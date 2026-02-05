package com.example.ozmade.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthNavHost(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isRegisterMode by remember { mutableStateOf(false) }

    // Observe State changes
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onAuthSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = uiState) {
            is AuthUiState.Loading -> CircularProgressIndicator()
            is AuthUiState.OtpRequired -> OtpScreen(
                phone = state.phone,
                onVerify = { otp -> viewModel.onVerifyOtp(state.phone, otp) }
            )
            else -> {
                if (isRegisterMode) {
                    RegisterScreen(
                        onRegisterBuyer = { phone -> viewModel.onRegisterBuyer(phone) },
                        onSwitchToLogin = { isRegisterMode = false }
                    )
                } else {
                    LoginScreen(
                        onLogin = { phone, pass -> viewModel.onLogin(phone, pass) },
                        onGoogleLogin = { viewModel.onGoogleSignIn() },
                        onSwitchToRegister = { isRegisterMode = true }
                    )
                }
            }
        }

        if (uiState is AuthUiState.Error) {
            Text(text = (uiState as AuthUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onGoogleLogin: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Text("Welcome Back", style = MaterialTheme.typography.headlineMedium)
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation()
    )

    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = { onLogin(phone, password) }, modifier = Modifier.fillMaxWidth()) {
        Text("Login")
    }

    // Google Sign In Button
    OutlinedButton(onClick = onGoogleLogin, modifier = Modifier.fillMaxWidth()) {
        Text("Sign in with Google")
    }

    TextButton(onClick = onSwitchToRegister) {
        Text("Don't have an account? Register")
    }
}

@Composable
fun RegisterScreen(
    onRegisterBuyer: (String) -> Unit,
    onSwitchToLogin: () -> Unit
) {
    var phone by remember { mutableStateOf("") }

    Text("Create Account", style = MaterialTheme.typography.headlineMedium)
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })

    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = { onRegisterBuyer(phone) }, modifier = Modifier.fillMaxWidth()) {
        Text("Request OTP")
    }

    // Note: Seller Registration usually happens in Profile -> "Become Seller"
    // or you can add a tab here if you want strict separation at start.

    TextButton(onClick = onSwitchToLogin) {
        Text("Already have an account? Login")
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