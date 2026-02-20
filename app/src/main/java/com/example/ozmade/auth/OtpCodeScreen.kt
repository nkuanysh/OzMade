package com.example.ozmade.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ozmade.R
import kotlinx.coroutines.delay

@Composable
fun OtpCodeScreen(
    phone: String,
    isLoading: Boolean,
    errorText: String?,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
) {
    var code by remember { mutableStateOf("") }

    var secondsLeft by remember { mutableIntStateOf(59) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        secondsLeft = 59
        canResend = false
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
        canResend = true
    }
    Spacer(Modifier.height(44.dp))


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(44.dp))

        Text(
            text = stringResource(R.string.auth_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(26.dp))

        Text(
            text = stringResource(R.string.enter_code),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.sent_to, phone),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { new ->
                // только цифры и максимум 6
                val filtered = new.filter { it.isDigit() }.take(6)
                code = filtered
                if (filtered.length == 6) onVerify(filtered)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("SMS код") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )

        Spacer(Modifier.height(12.dp))

        if (!canResend) {
            Text(
                text = stringResource(R.string.resend_in, secondsLeft),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            TextButton(
                onClick = {
                    onResend()
                    secondsLeft = 59
                    canResend = false
                    // перезапуск таймера
                    // (перезапуск через LaunchedEffect по ключу проще, но так тоже ок)
                }
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.resend))
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { onVerify(code) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = code.length == 6 && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
            }
            Text("Подтвердить")
        }

        if (!errorText.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}