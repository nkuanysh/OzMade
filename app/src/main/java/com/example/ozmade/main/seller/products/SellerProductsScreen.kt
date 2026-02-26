package com.example.ozmade.main.seller.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Добавить товар")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Мои товары",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск") },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SellerProductsFilter.entries) { f ->
                    FilterChip(
                        selected = state.filter == f,
                        onClick = { onFilterChange(f) },
                        label = { Text(f.title) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            when {
                state.loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(state.error, color = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = onRetry) { Text("Повторить") }
                                TextButton(onClick = onDismissError) { Text("Ок") }
                            }
                        }
                    }
                }

                state.filtered.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Товаров пока нет")
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.filtered, key = { it.id }) { p ->
                            SellerProductCard(
                                product = p,
                                onOpen = { onOpenEdit(p.id) },
                                onEditPrice = { priceDialogFor = p },
                                onToggleSale = { onToggleSale(p.id) },
                                onDelete = { deleteDialogFor = p }
                            )
                        }

                        item { Spacer(Modifier.height(80.dp)) } // чтобы FAB не перекрывал список
                    }
                }
            }
        }

        // Dropdown menu (3 точки)
//        val selected = menuFor
//        if (selected != null) {
//            SellerProductDropdown(
//                product = selected,
//                onDismiss = { menuFor = null },
//                onEditPrice = { priceDialogFor = selected; menuFor = null },
//                onToggleSale = { onToggleSale(selected.id); menuFor = null },
//                onDelete = { deleteDialogFor = selected; menuFor = null }
//            )
//        }

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
                title = { Text("Удалить товар?") },
                text = { Text("Товар “${delP.title}” будет удалён без возможности восстановления.") },
                confirmButton = {
                    Button(onClick = {
                        onDelete(delP.id)
                        deleteDialogFor = null
                    }) { Text("Удалить") }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${product.price} ₸",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(6.dp))

                AssistChip(
                    onClick = { },
                    label = { Text(product.status.title()) }
                )
            }

            var menuExpanded by remember { mutableStateOf(false) }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                }

                val toggleTitle = when (product.status) {
                    SellerProductStatus.ON_SALE -> "Снять с продажи"
                    SellerProductStatus.OFF_SALE -> "Выставить на продажу"
                    SellerProductStatus.PENDING_MODERATION -> "Остановить проверку"
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Изменить цену") },
                        onClick = {
                            menuExpanded = false
                            onEditPrice()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(toggleTitle) },
                        onClick = {
                            menuExpanded = false
                            onToggleSale()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Удалить товар") },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangePriceDialog(
    initial: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var value by remember { mutableStateOf(initial.toString()) }
    val parsed = value.toIntOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Изменить цену") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { v -> value = v.filter { it.isDigit() } },
                label = { Text("Цена (₸)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { if (parsed != null) onConfirm(parsed) },
                enabled = parsed != null && parsed > 0
            ) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}