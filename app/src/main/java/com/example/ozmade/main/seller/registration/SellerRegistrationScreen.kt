package com.example.ozmade.main.seller.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ozmade.network.model.SellerRegistrationRequestDto

private val categoryOptions = listOf(
    "Еда",
    "Одежда",
    "Искусство",
    "Ремесло",
    "Подарки",
    "Для дома"
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

    val orangeColor = Color(0xFFFF9800)
    val scrollState = rememberScrollState()

    val canContinue = firstName.isNotBlank() && lastName.isNotBlank() &&
            displayName.isNotBlank() && city.isNotBlank() &&
            address.isNotBlank() && selectedCategories.isNotEmpty() &&
            acceptedTerms && !isLoading

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = orangeColor,
        focusedLabelColor = orangeColor,
        cursorColor = orangeColor,
        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Стать продавцом", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Заполните данные — это поможет покупателям доверять вам.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // ---- Блок 1: Личные данные
                SectionCard(title = "Профиль продавца") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F0F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        OutlinedButton(
                            onClick = { /* TODO */ },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = orangeColor)
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Фото магазина")
                        }
                    }

                    CustomTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "Имя *",
                        icon = Icons.Default.Badge,
                        colors = textFieldColors
                    )
                    Spacer(Modifier.height(12.dp))
                    CustomTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Фамилия *",
                        icon = Icons.Default.Badge,
                        colors = textFieldColors
                    )
                    Spacer(Modifier.height(12.dp))
                    CustomTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = "Название магазина *",
                        placeholder = "Напр: Мастерская уютных вещей",
                        icon = Icons.Default.Storefront,
                        colors = textFieldColors
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ---- Блок 2: Локация
                SectionCard(title = "Местоположение") {
                    CustomTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "Город *",
                        icon = Icons.Default.LocationCity,
                        colors = textFieldColors
                    )
                    Spacer(Modifier.height(12.dp))
                    CustomTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "Адрес *",
                        icon = Icons.Default.Place,
                        colors = textFieldColors
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ---- Блок 3: Категории и описание
                SectionCard(title = "О товарах") {
                    Text(
                        "Выберите категории товаров:",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categoryOptions.forEach { cat ->
                            val selected = selectedCategories.contains(cat)
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedCategories = if (selected) selectedCategories - cat else selectedCategories + cat
                                },
                                label = { Text(cat) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = orangeColor.copy(alpha = 0.1f),
                                    selectedLabelColor = orangeColor,
                                    selectedLeadingIconColor = orangeColor
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (selected) orangeColor else Color.LightGray,
                                    borderWidth = 1.dp,
                                    enabled = true,
                                    selected = selected
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = about,
                        onValueChange = { about = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Описание деятельности") },
                        placeholder = { Text("Расскажите коротко о ваших товарах...") },
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors
                    )
                }

                Spacer(Modifier.height(24.dp))

                // ---- Согласия
                Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                    TermsLinks(onTerms = onOpenSellerTerms, onPrivacy = onOpenPrivacy, accentColor = orangeColor)
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { acceptedTerms = !acceptedTerms }.padding(vertical = 8.dp)
                    ) {
                        Checkbox(
                            checked = acceptedTerms, 
                            onCheckedChange = { acceptedTerms = it },
                            colors = CheckboxDefaults.colors(checkedColor = orangeColor)
                        )
                        Text(
                            text = "Принимаю условия продавца",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (!errorText.isNullOrBlank()) {
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = canContinue,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orangeColor, disabledContainerColor = Color.LightGray)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(Modifier.width(12.dp))
                    }
                    Text("Создать магазин", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(32.dp))
            }

            if (showInstruction) {
                AlertDialog(
                    onDismissRequest = { showInstruction = false },
                    title = { Text("Добро пожаловать!") },
                    text = { Text("Чтобы начать продавать эффективно, ознакомьтесь с нашей краткой инструкцией.") },
                    confirmButton = {
                        Button(
                            onClick = { showInstruction = false },
                            colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Понятно")
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    icon: ImageVector,
    colors: TextFieldColors
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, fontSize = 14.sp) },
        leadingIcon = { Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.Gray) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = colors
    )
}

@Composable
private fun TermsLinks(onTerms: () -> Unit, onPrivacy: () -> Unit, accentColor: Color) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Gray)) {
            append("Нажимая кнопку, вы соглашаетесь с ")
        }
        pushStringAnnotation(tag = "terms", annotation = "terms")
        withStyle(style = SpanStyle(color = accentColor, fontWeight = FontWeight.Bold)) {
            append("Условиями")
        }
        pop()
        withStyle(style = SpanStyle(color = Color.Gray)) {
            append(" и ")
        }
        pushStringAnnotation(tag = "privacy", annotation = "privacy")
        withStyle(style = SpanStyle(color = accentColor, fontWeight = FontWeight.Bold)) {
            append("Политикой конфиденциальности")
        }
        pop()
    }

    androidx.compose.foundation.text.ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "terms", start = offset, end = offset).firstOrNull()?.let { onTerms() }
            annotatedString.getStringAnnotations(tag = "privacy", start = offset, end = offset).firstOrNull()?.let { onPrivacy() }
        }
    )
}
