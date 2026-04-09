package com.example.ozmade.main.seller.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

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
fun SellerStoreSettingsScreen(
    onBack: () -> Unit,
    viewModel: SellerStoreSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val orangeAccent = Color(0xFFFF9800)
    val backgroundColor = Color(0xFFFBFBFB)

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.onLogoSelected(uri)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Настройки магазина", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error Message
            AnimatedVisibility(visible = uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            uiState.error ?: "",
                            modifier = Modifier.weight(1f),
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = { viewModel.dismissError() }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }

            // Logo Section
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                val imageSource = uiState.localLogoUri ?: uiState.logoUrl
                if (imageSource != null) {
                    AsyncImage(
                        model = imageSource,
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = "Add Photo",
                        tint = orangeAccent,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Overlay icon
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(orangeAccent, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddAPhoto, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            
            Text(
                "Логотип магазина",
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            // --- Блок 1: Личные данные ---
            SettingsSection(title = "Личные данные") {
                SettingsTextField(
                    value = uiState.firstName,
                    onValueChange = viewModel::onFirstNameChange,
                    label = "Имя",
                    placeholder = "Введите имя",
                    orangeAccent = orangeAccent,
                    readOnly = true
                )
                Spacer(Modifier.height(16.dp))
                SettingsTextField(
                    value = uiState.lastName,
                    onValueChange = viewModel::onLastNameChange,
                    label = "Фамилия",
                    placeholder = "Введите фамилию",
                    orangeAccent = orangeAccent,
                    readOnly = true
                )
            }

            Spacer(Modifier.height(24.dp))

            // --- Блок 2: О магазине ---
            SettingsSection(title = "Информация о магазине") {
                SettingsTextField(
                    value = uiState.storeName,
                    onValueChange = viewModel::onStoreNameChange,
                    label = "Название магазина",
                    placeholder = "Введите название",
                    orangeAccent = orangeAccent
                )
                Spacer(Modifier.height(16.dp))
                SettingsTextField(
                    value = uiState.about,
                    onValueChange = viewModel::onAboutChange,
                    label = "Описание магазина",
                    placeholder = "Расскажите о своих товарах",
                    orangeAccent = orangeAccent,
                    singleLine = false,
                    minLines = 3
                )
            }

            Spacer(Modifier.height(24.dp))

            // --- Блок 3: Локация ---
            SettingsSection(title = "Местоположение") {
                SettingsTextField(
                    value = uiState.city,
                    onValueChange = viewModel::onCityChange,
                    label = "Город",
                    placeholder = "Напр: Алматы",
                    orangeAccent = orangeAccent
                )
                Spacer(Modifier.height(16.dp))
                SettingsTextField(
                    value = uiState.address,
                    onValueChange = viewModel::onAddressChange,
                    label = "Адрес",
                    placeholder = "Улица, дом, офис",
                    orangeAccent = orangeAccent
                )
            }

            Spacer(Modifier.height(40.dp))

            // Save Button
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                enabled = !uiState.isLoading && uiState.storeName.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Сохранить изменения", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    orangeAccent: Color,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            readOnly = readOnly,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = orangeAccent,
                unfocusedBorderColor = Color.LightGray.copy(0.4f),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
    }
}
