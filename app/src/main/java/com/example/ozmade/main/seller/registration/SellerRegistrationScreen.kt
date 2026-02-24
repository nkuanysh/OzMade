package com.example.ozmade.main.seller.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.filled.Add
import com.example.ozmade.network.model.SellerRegistrationRequestDto
private val categoryOptions = listOf(
    "Еда (выпечка/десерты)",
    "Одежда (аксессуары ручной работы)",
    "Ремесло/хендмейд",
    "Искусство (арт/декор)",
    "Другое"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SellerRegistrationScreen(
    onBack: () -> Unit,
    onOpenSellerTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onSubmit: (SellerRegistrationRequestDto) -> Unit,
    isLoading: Boolean,
    errorText: String?
) {
    var showInstruction by remember { mutableStateOf(true) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }

    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    var acceptedTerms by remember { mutableStateOf(false) }

    val canContinue =
        firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                displayName.isNotBlank() &&
                city.isNotBlank() &&
                address.isNotBlank() &&
                selectedCategories.isNotEmpty() &&
                acceptedTerms &&
                !isLoading

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Регистрация продавца") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->

        Box(Modifier.fillMaxSize().padding(padding)) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Заполните данные — это поможет покупателям доверять вам.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // ---- Блок 1: профиль продавца
                Text("Профиль продавца", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {}
                    }
                    Spacer(Modifier.width(12.dp))
                    OutlinedButton(onClick = { /* TODO выбрать фото */ }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Добавить фото")
                    }
                }

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Имя *") },
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Фамилия *") },
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Отображаемое имя / псевдоним *") },
                    placeholder = { Text("Домашние вареники Айгуль, QolOner by Nurs") },
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Город *") },
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Адрес *") },
                )

                Spacer(Modifier.height(18.dp))

                // ---- Блок 2: категории
                Text("Что продаёте", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryOptions.forEach { cat ->
                        val selected = selectedCategories.contains(cat)
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedCategories =
                                    if (selected) selectedCategories - cat
                                    else selectedCategories + cat
                            },
                            label = { Text(cat) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = about,
                    onValueChange = { about = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Короткое описание (необязательно)") },
                    placeholder = { Text("Готовлю домашнюю выпечку на заказ…") },
                    minLines = 3
                )

                Spacer(Modifier.height(20.dp))

                // ---- Ссылки: условия/политика (синие кликабельные)
                val linksText = buildAnnotatedString {
                    pushStringAnnotation(tag = "terms", annotation = "terms")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Условия продавца")
                    }
                    pop()

                    append(" • ")

                    pushStringAnnotation(tag = "privacy", annotation = "privacy")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Политика конфиденциальности")
                    }
                    pop()
                }

                ClickableText(
                    text = linksText,
                    style = TextStyle(textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSurface),
                    onClick = { offset ->
                        linksText.getStringAnnotations("terms", offset, offset).firstOrNull()?.let {
                            onOpenSellerTerms(); return@ClickableText
                        }
                        linksText.getStringAnnotations("privacy", offset, offset).firstOrNull()?.let {
                            onOpenPrivacy(); return@ClickableText
                        }
                    }
                )

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = acceptedTerms, onCheckedChange = { acceptedTerms = it })
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Я ознакомился(лась) и принимаю условия продавца",
                        modifier = Modifier.clickable { acceptedTerms = !acceptedTerms }
                    )
                }

                if (!errorText.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(errorText, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        onSubmit(
                            SellerRegistrationRequestDto(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                displayName = displayName.trim(),
                                city = city.trim(),
                                address = address.trim(),
                                categories = selectedCategories.toList(),
                                about = about.trim().ifBlank { null }
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = canContinue,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                    }
                    Text("Продолжить")
                }

                Spacer(Modifier.height(16.dp))
            }

            // ✅ Модалка-инструкция поверх экрана
            if (showInstruction) {
                AlertDialog(
                    onDismissRequest = { showInstruction = false },
                    title = { Text("Инструкция для продавцов") },
                    text = { Text("Чтобы продавать товары эффективнее прочитайте инструкцию для продавцов") },
                    confirmButton = {
                        Button(onClick = { /* TODO открыть инструкцию */ }) {
                            Text("Изучить инструкцию")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showInstruction = false }) {
                            Text("Закрыть")
                        }
                    }
                )
            }
        }
    }
}