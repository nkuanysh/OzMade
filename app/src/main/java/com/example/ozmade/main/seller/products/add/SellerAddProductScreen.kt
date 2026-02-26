package com.example.ozmade.main.seller.products.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
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

    val pickImages = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        if (uris.isNotEmpty()) onPickPhotos(uris)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Добавление товара") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } // ✅ уведомления снизу
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (state.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            state.error,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        TextButton(onClick = onDismissError) { Text("Ок") }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Text("Загрузите от 1 до 10 фото")

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    pickImages.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                enabled = state.canAddMorePhotos,
                modifier = Modifier.heightIn(min = 52.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (state.canAddMorePhotos) "Добавить фото" else "Лимит 10 фото")
            }

            Spacer(Modifier.height(12.dp))

            if (state.photos.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(state.photos, key = { _, uri -> uri.toString() }) { index, uri ->
                        PhotoThumb(
                            uri = uri,
                            index = index,
                            total = state.photos.size,
                            onRemove = { onRemovePhoto(uri) },
                            onMoveLeft = { if (index > 0) onMovePhoto(index, index - 1) },
                            onMoveRight = { if (index < state.photos.lastIndex) onMovePhoto(index, index + 1) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Зажмите и перетащите фото, чтобы изменить порядок",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = { showRequirements = true }) {
                Text("Требования к фотографиям")
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.title,
                onValueChange = onTitle,
                label = { Text("Название товара") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.priceText,
                onValueChange = { v ->
                    // разрешаем цифры, точку и запятую
                    onPrice(v.filter { it.isDigit() || it == '.' || it == ',' })
                },
                label = { Text("Цена") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(Modifier.height(12.dp))

            Text("Категории")
            Spacer(Modifier.height(8.dp))

            FlowRowChips(
                selected = state.selectedCategories,
                onToggle = onToggleCategory
            )

            Spacer(Modifier.height(18.dp))

            Text("Характеристики", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = state.weightText,
                onValueChange = { onWeight(it) },
                label = { Text("Вес") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.heightText,
                    onValueChange = { onHeight(it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                    label = { Text("Высота (см)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = state.widthText,
                    onValueChange = { onWidth(it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                    label = { Text("Ширина (см)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = state.depthText,
                    onValueChange = { onDepth(it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                    label = { Text("Глубина (см)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.composition,
                onValueChange = onComposition,
                label = { Text("Состав") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(18.dp))

            Text("Описание", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescription,
                label = { Text("Описание") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                maxLines = 6
            )

            Spacer(Modifier.height(18.dp))

            Text("Добавьте ссылку на YouTube")
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.youtubeUrl,
                onValueChange = onYoutube,
                label = { Text("Ссылка (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onCreate,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                enabled = state.isValid && !state.loading
            ) {
                if (state.loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                }
                Text("Добавить товар")
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showRequirements) {
        AlertDialog(
            onDismissRequest = { showRequirements = false },
            title = { Text("Требования к фотографиям") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("• 1–10 фото")
                    Text("• Хорошее освещение, без сильных теней")
                    Text("• Товар крупно и без лишнего фона")
                    Text("• Без чужих водяных знаков/логотипов")
                }
            },
            confirmButton = {
                Button(onClick = { showRequirements = false }) { Text("Понятно") }
            }
        )
    }
}

@Composable
private fun PhotoThumb(
    uri: Uri,
    index: Int,
    total: Int,
    onRemove: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit
) {
    Box(
        modifier = Modifier.size(92.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        // крестик
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.Close, contentDescription = null)
        }

        // маленькие кнопки перестановки (чтобы точно работало)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = onMoveLeft,
                enabled = index > 0
            ) { Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null) }

            IconButton(
                onClick = onMoveRight,
                enabled = index < total - 1
            ) { Icon(Icons.Default.KeyboardArrowRight, contentDescription = null) }
        }
    }
}

@Composable
private fun FlowRowChips(
    selected: Set<SellerCategory>,
    onToggle: (SellerCategory) -> Unit
) {
    // Без внешних библиотек: сделаем “перенос” через простую сетку 2 колонки
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val all = SellerCategory.entries
        for (row in all.chunked(2)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { c ->
                    FilterChip(
                        selected = selected.contains(c),
                        onClick = { onToggle(c) },
                        label = { Text(c.title) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}