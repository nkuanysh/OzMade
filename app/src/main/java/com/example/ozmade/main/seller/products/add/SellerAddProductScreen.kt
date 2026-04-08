package com.example.ozmade.main.seller.products.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SellerAddProductScreen(
    state: AddProductState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onPickPhotos: (List<Uri>) -> Unit,
    onRemovePhoto: (Uri) -> Unit,
    onMovePhoto: (from: Int, to: Int) -> Unit,
    onTitle: (String) -> Unit,
    onPrice: (String) -> Unit,
    onToggleCategory: (SellerCategory) -> Unit,
    onWeight: (String) -> Unit,
    onHeight: (String) -> Unit,
    onWidth: (String) -> Unit,
    onDepth: (String) -> Unit,
    onComposition: (String) -> Unit,
    onDescription: (String) -> Unit,
    onYoutube: (String) -> Unit,
    onCreate: () -> Unit,
    onDismissError: () -> Unit
) {
    var showRequirements by remember { mutableStateOf(false) }
    val orangeAccent = Color(0xFFFF9800)
    val backgroundColor = Color(0xFFFBFBFB)

    val pickImages = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        if (uris.isNotEmpty()) onPickPhotos(uris)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Добавление товара", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Error handling
            AnimatedVisibility(visible = state.error != null) {
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
                            state.error ?: "",
                            modifier = Modifier.weight(1f),
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = onDismissError) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }

            // Photo section
            Text("Фотографии товара", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Добавьте от 1 до 10 качественных фото", color = Color.Gray, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 12.dp)
            ) {
                item {
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable(enabled = state.canAddMorePhotos && !state.loading) {
                                pickImages.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = border(state.canAddMorePhotos, orangeAccent)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = if (state.canAddMorePhotos) orangeAccent else Color.LightGray
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (state.canAddMorePhotos) "Добавить" else "Лимит",
                                fontSize = 12.sp,
                                color = if (state.canAddMorePhotos) orangeAccent else Color.LightGray
                            )
                        }
                    }
                }
                itemsIndexed(state.photos, key = { _, uri -> uri.toString() }) { index, uri ->
                    PhotoThumb(
                        uri = uri,
                        index = index,
                        total = state.photos.size,
                        onRemove = { onRemovePhoto(uri) },
                        onMoveLeft = { if (index > 0) onMovePhoto(index, index - 1) },
                        onMoveRight = { if (index < state.photos.lastIndex) onMovePhoto(index, index + 1) },
                        accentColor = orangeAccent
                    )
                }
            }

            TextButton(
                onClick = { showRequirements = true },
                colors = ButtonDefaults.textButtonColors(contentColor = orangeAccent),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Требования к фотографиям", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))

            // Main Info
            SectionHeader("Основная информация")
            
            CustomEditField(
                value = state.title,
                onValueChange = onTitle,
                label = "Название товара",
                placeholder = "Напр: Глиняная ваза ручной работы",
                orangeAccent = orangeAccent
            )

            Spacer(Modifier.height(12.dp))

            CustomEditField(
                value = state.priceText,
                onValueChange = { v -> onPrice(v.filter { it.isDigit() || it == '.' || it == ',' }) },
                label = "Цена (₸)",
                keyboardType = KeyboardType.Number,
                orangeAccent = orangeAccent
            )

            Spacer(Modifier.height(20.dp))

            Text("Категории", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SellerCategory.entries.forEach { cat ->
                    val selected = state.selectedCategories.contains(cat)
                    FilterChip(
                        selected = selected,
                        onClick = { onToggleCategory(cat) },
                        label = { Text(cat.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = orangeAccent.copy(0.1f),
                            selectedLabelColor = orangeAccent,
                            selectedLeadingIconColor = orangeAccent
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selected) orangeAccent else Color.LightGray,
                            borderWidth = 1.dp,
                            enabled = true,
                            selected = selected
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Specs
            SectionHeader("Характеристики")
            
            CustomEditField(
                value = state.weightText,
                onValueChange = onWeight,
                label = "Вес (напр: 500г или 1.2кг)",
                orangeAccent = orangeAccent
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    CustomEditField(
                        value = state.heightText,
                        onValueChange = { onHeight(it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                        label = "Высота (см)",
                        keyboardType = KeyboardType.Number,
                        orangeAccent = orangeAccent
                    )
                }
                Box(Modifier.weight(1f)) {
                    CustomEditField(
                        value = state.widthText,
                        onValueChange = { onWidth(it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                        label = "Ширина (см)",
                        keyboardType = KeyboardType.Number,
                        orangeAccent = orangeAccent
                    )
                }
                Box(Modifier.weight(1f)) {
                    CustomEditField(
                        value = state.depthText,
                        onValueChange = { onDepth(it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                        label = "Глубина (см)",
                        keyboardType = KeyboardType.Number,
                        orangeAccent = orangeAccent
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            CustomEditField(
                value = state.composition,
                onValueChange = onComposition,
                label = "Состав / Материалы",
                placeholder = "Напр: 100% хлопок, натуральная кожа",
                orangeAccent = orangeAccent
            )

            Spacer(Modifier.height(24.dp))

            // Description
            SectionHeader("Описание")
            OutlinedTextField(
                value = state.description,
                onValueChange = onDescription,
                label = { Text("Подробное описание товара") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = orangeAccent,
                    focusedLabelColor = orangeAccent,
                    unfocusedBorderColor = Color.LightGray.copy(0.4f),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(Modifier.height(16.dp))

            CustomEditField(
                value = state.youtubeUrl,
                onValueChange = onYoutube,
                label = "Ссылка на видео YouTube (необязательно)",
                placeholder = "https://www.youtube.com/watch?v=...",
                orangeAccent = orangeAccent,
                keyboardType = KeyboardType.Uri
            )

            Spacer(Modifier.height(32.dp))

            // Actions
            Button(
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                enabled = state.isValid && !state.loading
            ) {
                if (state.loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Добавить товар", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showRequirements) {
        AlertDialog(
            onDismissRequest = { showRequirements = false },
            title = { Text("Требования к фото", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    RequirementItem("1–10 фотографий высокого качества")
                    RequirementItem("Хорошее освещение, без сильных теней")
                    RequirementItem("Товар в центре кадра на чистом фоне")
                    RequirementItem("Без водяных знаков и чужих логотипов")
                }
            },
            confirmButton = {
                TextButton(onClick = { showRequirements = false }, colors = ButtonDefaults.textButtonColors(contentColor = orangeAccent)) {
                    Text("Понятно", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun border(enabled: Boolean, color: Color) = if (enabled) {
    androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.5f))
} else {
    androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(0.3f))
}

@Composable
private fun RequirementItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun CustomEditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    orangeAccent: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = orangeAccent,
            focusedLabelColor = orangeAccent,
            unfocusedBorderColor = Color.LightGray.copy(0.4f),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}

@Composable
private fun PhotoThumb(
    uri: Uri,
    index: Int,
    total: Int,
    onRemove: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    accentColor: Color
) {
    Box(modifier = Modifier.size(100.dp)) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, Color.LightGray.copy(0.3f), RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp).clickable { onRemove() },
            shape = CircleShape,
            color = Color.Black.copy(0.5f)
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
        }

        if (total > 1) {
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp).background(Color.Black.copy(0.4f), CircleShape).padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onMoveLeft, enabled = index > 0, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = if (index > 0) Color.White else Color.Gray, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onMoveRight, enabled = index < total - 1, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = if (index < total - 1) Color.White else Color.Gray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
