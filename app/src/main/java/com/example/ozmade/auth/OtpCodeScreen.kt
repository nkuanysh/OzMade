package com.example.ozmade.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OtpCodeScreen(
    phone: String,
    isLoading: Boolean = false,
    errorText: String? = null,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val orangePrimary = Color(0xFFFF7A1A)
    val background = MaterialTheme.colorScheme.background
    val darkNavy = Color(0xFF0D0F2C)
    val secondaryText = Color(0xFFCFCFCF)
    val boxBg = Color(0xFFE8E4F0) // Light purple/grey background from image

    var otpCode by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    
    var secondsLeft by remember { mutableIntStateOf(59) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = canResend) {
        if (!canResend) {
            secondsLeft = 59
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft -= 1
            }
            canResend = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        // Top Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .background(
                    color = darkNavy,
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Circular Back Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Проверка",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Мы отправили код подтверждения на ",
                    color = secondaryText,
                    fontSize = 14.sp
                )
                Text(
                    text = phone,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Bottom Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "КОД",
                modifier = Modifier.align(Alignment.Start),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // OTP Input Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                otpCode.forEachIndexed { index, digit ->
                    var isFocused by remember { mutableStateOf(false) }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(boxBg, RoundedCornerShape(12.dp))
                            .border(
                                width = 2.dp,
                                color = if (isFocused) orangePrimary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = digit,
                            onValueChange = { newValue ->
                                if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                    val newCode = otpCode.toMutableList()
                                    newCode[index] = newValue
                                    otpCode = newCode
                                    
                                    // Auto focus move
                                    if (newValue.isNotEmpty() && index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .focusRequester(focusRequesters[index])
                                .onFocusChanged { isFocused = it.isFocused }
                                .onKeyEvent { event ->
                                    if (event.type == KeyEventType.KeyUp && event.key == Key.Backspace && digit.isEmpty() && index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                        true
                                    } else {
                                        false
                                    }
                                },
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(orangePrimary),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resend Timer
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (!canResend) {
                    Text(
                        text = "Повторно отправить код через ${secondsLeft} сек",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "Отправить повторно",
                        color = orangePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { 
                            onResend()
                            canResend = false
                        }
                    )
                }
            }

            if (!errorText.isNullOrEmpty()) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onVerify(otpCode.joinToString("")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangePrimary),
                shape = RoundedCornerShape(16.dp),
                enabled = otpCode.all { it.isNotEmpty() } && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "ПРОВЕРИТЬ",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
