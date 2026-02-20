package com.example.ozmade.main.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.chat.data.ChatMessageUi
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.platform.LocalFocusManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatThreadRoute(
    sellerId: String,
    productId: String,
    sellerName: String,
    productTitle: String,
    productPrice: Int,
    onBack: () -> Unit,
    onOpenProduct: (String) -> Unit,
    viewModel: ChatThreadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sellerId, productId) {
        viewModel.openChat(
            sellerId = sellerId,
            sellerName = sellerName,
            productId = productId,
            productTitle = productTitle,
            productPrice = productPrice
        )
    }

    ChatThreadScreen(
        uiState = uiState,
        onBack = onBack,
        onSend = { viewModel.send(it) },
        onOpenProduct = { onOpenProduct(productId) }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatThreadScreen(
    uiState: ChatThreadUiState,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onOpenProduct: () -> Unit
) {
    var input by remember { mutableStateOf("") }

    // меню в topbar
    var menuExpanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val seller = (uiState as? ChatThreadUiState.Data)?.sellerName ?: "Чат"
                    Text(seller, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Удалить чат") },
                                onClick = {
                                    menuExpanded = false
                                    // TODO: delete chat action
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Заблокировать") },
                                onClick = {
                                    menuExpanded = false
                                    // TODO: block action
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Пожаловаться") },
                                onClick = {
                                    menuExpanded = false
                                    // TODO: report action
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            // красивый input bar
            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        // TODO: emoji panel
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.Default.Face, contentDescription = null)
                    }

                    // поле ввода
                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp),
                        placeholder = { Text("Сообщение…") },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.width(4.dp))

                    IconButton(onClick = {
                        // TODO: attach
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }

                    val trimmed = input.trim()

                    // справа: если пусто -> mic, если есть текст -> send
                    if (trimmed.isEmpty()) {
                        IconButton(onClick = {
                            // TODO: audio record
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                        }
                    } else {
                        FilledIconButton(
                            onClick = {
                                onSend(trimmed)
                                input = ""
                                focusManager.clearFocus()
                            },
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null)
                        }
                    }
                }
            }
        }
    ) { padding ->

        when (uiState) {
            is ChatThreadUiState.Loading -> {
                Box(
                    Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is ChatThreadUiState.Error -> {
                Box(
                    Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(uiState.message, color = MaterialTheme.colorScheme.error) }
            }

            is ChatThreadUiState.Data -> {
                Column(Modifier.padding(padding).fillMaxSize()) {

                    ProductContextBar(
                        title = uiState.productTitle,
                        price = uiState.productPrice,
                        onClick = onOpenProduct
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.messages, key = { it.id }) { msg ->
                            Bubble(msg)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductContextBar(
    title: String,
    price: Int,
    onClick: () -> Unit
) {
    Surface(tonalElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleSmall)
                Text("$price ₸", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
private fun Bubble(msg: ChatMessageUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (msg.isMine) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(12.dp)
        ) {
            Text(msg.text, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(6.dp))
            Text(msg.timeText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
