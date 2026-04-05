package com.example.ozmade.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.R

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
            errorText = (uiState as? AuthUiState.Error)?.message,
            onVerify = { otp -> viewModel.verifyOtp(state.verificationId, otp) },
            onResend = { viewModel.requestOtp(activity, state.phone) }
        )

        is AuthUiState.Loading -> PhoneLoginScreen(
            isLoading = true,
            errorText = null,
            onSendCode = { phone -> viewModel.requestOtp(activity, phone) },
            onBackClick = onBack,
            onOpenPrivacy = onOpenPrivacy,
            onOpenTerms = onOpenTerms
        )

        is AuthUiState.Error -> PhoneLoginScreen(
            isLoading = false,
            errorText = state.message,
            onSendCode = { phone -> viewModel.requestOtp(activity, phone) },
            onBackClick = onBack,
            onOpenPrivacy = onOpenPrivacy,
            onOpenTerms = onOpenTerms
        )

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

@Composable
fun PhoneLoginScreen(
    isLoading: Boolean = false,
    errorText: String? = null,
    onSendCode: (String) -> Unit,
    onBackClick: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {},
    onOpenTerms: () -> Unit = {}
) {
    var phoneNumber by remember { mutableStateOf("") }

    val darkNavy = Color(0xFF0D0F2C)
    val lightGray = Color(0xFFF2F2F2)
    val orangePrimary = Color(0xFFFF7A1A)
    val secondaryText = Color(0xFFCFCFCF)
    val inputBg = Color(0xFFE5E5E5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGray)
    ) {
        // Top Section with Logo and Back Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = darkNavy,
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Back Button
                Box(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .size(40.dp)
                        .background(inputBg, CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = darkNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // OzMade Logo
                Image(
                    painter = painterResource(id = R.drawable.image_removebg_preview),
                    contentDescription = "OzMade Logo",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Добро пожаловать!",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Введите свой номер телефона, чтобы продолжить",
                    color = secondaryText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom Section with Input and Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "НОМЕР ТЕЛЕФОНА",
                modifier = Modifier.align(Alignment.Start),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = { Text("+7 777 123 45 67", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = inputBg,
                    unfocusedContainerColor = inputBg,
                    disabledContainerColor = inputBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (errorText != null) {
                Text(
                    text = errorText,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSendCode(phoneNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangePrimary),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "ОТПРАВИТЬ КОД",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Privacy and Terms
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Privacy Policy",
                    modifier = Modifier.clickable { onOpenPrivacy() },
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = " | ",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = "Terms of Service",
                    modifier = Modifier.clickable { onOpenTerms() },
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhoneLoginScreenPreview() {
    PhoneLoginScreen(onSendCode = {})
}
