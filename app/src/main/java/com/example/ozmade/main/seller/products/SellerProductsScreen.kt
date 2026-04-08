package com.example.ozmade.main.seller.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProductsScreen(
    state: SellerProductsState,
    onRetry: () -> Unit,
    onQueryChange: (String) -> Unit,
    onFilterChange: (SellerProductsFilter) -> Unit,

    onAddProduct: () -> Unit,
    onOpenEdit: (Int) -> Unit,

    onUpdatePrice: (Int, Int) -> Unit,
    onToggleSale: (Int) -> Unit,
    onDelete: (Int) -> Unit,

    onDismissError: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {

    var priceDialogFor by remember { mutableStateOf<SellerProductUi?>(null) }
    var deleteDialogFor by remember { mutableStateOf<SellerProductUi?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Мои товары",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FA)
                )
            )
        },
        containerColor = Color(0xFFF8F9FA),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddProduct,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Добавить товар", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 1.dp,
                shadowElevation = 0.5.dp
            ) {
                TextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск по названию...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SellerProductsFilter.entries) { f ->
                    val isSelected = state.filter == f
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(f) },
                        label = { Text(f.title) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.LightGray.copy(alpha = 0.5f),
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            when {
                state.loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeWidth = 3.dp)
                    }
                }

                state.error != null -> {
                    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(state.error, color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(
                                        onClick = onRetry,
                                        shape = RoundedCornerShape(12.dp)
                                    ) { Text("Повторить") }
                                    TextButton(onClick = onDismissError) { Text("Ок") }
                                }
                            }
                        }
                    }
                }

                state.filtered.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Товаров пока нет",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.Gray
                        )
                        Text(
                            "Добавьте свой первый товар, чтобы начать продажи",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(state.filtered, key = { it.id }) { p ->
                            SellerProductCard(
                                product = p,
                                onOpen = { onOpenEdit(p.id) },
                                onEditPrice = { priceDialogFor = p },
                                onToggleSale = { onToggleSale(p.id) },
                                onDelete = { deleteDialogFor = p }
                            )
                        }
                    }
                }
            }
        }

        // Диалог изменения цены
        val priceP = priceDialogFor
        if (priceP != null) {
            ChangePriceDialog(
                initial = priceP.price,
                onDismiss = { priceDialogFor = null },
                onConfirm = { newPrice ->
                    onUpdatePrice(priceP.id, newPrice)
                    priceDialogFor = null
                }
            )
        }

        // Диалог удаления
        val delP = deleteDialogFor
        if (delP != null) {
            AlertDialog(
                onDismissRequest = { deleteDialogFor = null },
                shape = RoundedCornerShape(28.dp),
                title = { Text("Удалить товар?", fontWeight = FontWeight.Bold) },
                text = { Text("Товар “${delP.title}” будет удалён без возможности восстановления.") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete(delP.id)
                            deleteDialogFor = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Удалить") }
                },
                dismissButton = {
                    TextButton(onClick = { deleteDialogFor = null }) { Text("Отмена") }
                }
            )
        }
    }
}

@Composable
private fun SellerProductCard(
    product: SellerProductUi,
    onOpen: () -> Unit,
    onEditPrice: () -> Unit,
    onToggleSale: () -> Unit,
    onDelete: () -> Unit
) {
    val isOnSale = product.status == SellerProductStatus.ON_SALE

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onOpen() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        tonalElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                if (product.imageUrl != null) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = product.title.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.price} AUD",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isOnSale) Color(0xFF4CAF50) else Color.Gray)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isOnSale) "В продаже" else "Снят с продажи",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
            }

            var expanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Изменить цену") },
                        onClick = {
                            expanded = false
                            onEditPrice()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isOnSale) "Снять с продажи" else "Вернуть в продажу") },
                        onClick = {
                            expanded = false
                            onToggleSale()
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Удалить", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChangePriceDialog(
    initial: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var text by remember { mutableStateOf(initial.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Изменить цену", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Введите новую цену товара (AUD):", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.all { c -> c.isDigit() }) text = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    suffix = { Text("AUD") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = text.toIntOrNull() ?: 0
                    if (p > 0) onConfirm(p)
                },
                enabled = text.isNotEmpty(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
        shape = RoundedCornerShape(28.dp)
    )
}
