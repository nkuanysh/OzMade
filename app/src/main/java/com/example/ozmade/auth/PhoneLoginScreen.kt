package com.example.ozmade.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.ozmade.R
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.TextStyle

@Composable
fun PhoneLoginScreen(
    isLoading: Boolean,
    errorText: String?,
    onSendCode: (String) -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
) {
    var phone by remember { mutableStateOf("+7") }

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

        Spacer(Modifier.height(30.dp))

        Text(
            text = stringResource(R.string.enter_phone),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.sms_info),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(stringResource(R.string.enter_phone)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onSendCode(phone.trim()) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
            }
            Text(stringResource(R.string.send_code))
        }

        if (!errorText.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(12.dp))

        val annotated = buildAnnotatedString {
            // обычный черный текст
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                append(stringResource(R.string.continue_agree)) // уже содержит пробел в конце
            }

            // кликабельная "Политикой..."
            pushStringAnnotation(tag = "privacy", annotation = "privacy")
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary
                    // если хочешь подчеркнуть как ссылку, добавь:
                    // textDecoration = TextDecoration.Underline
                )
            ) {
                append(stringResource(R.string.privacy_policy))
            }
            pop()

            // обычное " и " (с пробелами)
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                append(stringResource(R.string.and_word))
            }

            // кликабельная "Пользовательским..."
            pushStringAnnotation(tag = "terms", annotation = "terms")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(stringResource(R.string.terms))
            }
            pop()
        }

        ClickableText(
            text = annotated,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            onClick = { offset ->
                annotated.getStringAnnotations(tag = "privacy", start = offset, end = offset)
                    .firstOrNull()
                    ?.let { onOpenPrivacy(); return@ClickableText }

                annotated.getStringAnnotations(tag = "terms", start = offset, end = offset)
                    .firstOrNull()
                    ?.let { onOpenTerms(); return@ClickableText }
            }
        )

        Spacer(Modifier.height(10.dp))
    }
}