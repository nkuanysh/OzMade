package com.example.ozmade.main.user.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ozmade.main.user.chat.data.ChatMessageUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatThreadRoute(
    chatId: Int?,
    sellerId: Int,
    productId: Int,
    sellerName: String,
    sellerPhotoUrl: String? = null,
    productTitle: String,
    productPrice: Int,
    onBack: () -> Unit,
    onOpenProduct: (Int) -> Unit,
    viewModel: ChatThreadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            if (event is ChatThreadViewModel.ChatEvent.ChatDeleted) {
                onBack()
            }
        }
    }

    LaunchedEffect(chatId, sellerId, productId) {
        viewModel.openChat(
            chatId = chatId,
            sellerId = sellerId,
            sellerName = sellerName,
            sellerPhotoUrl = sellerPhotoUrl,
            productId = productId,
            productTitle = productTitle,
            productPrice = productPrice
        )
    }

    ChatThreadScreen(
        uiState = uiState,
        onBack = onBack,
        onSend = { viewModel.send(it) },
        onDelete = { viewModel.deleteCurrentChat() },
        onOpenProduct = { onOpenProduct(productId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatThreadScreen(
    uiState: ChatThreadUiState,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onDelete: () -> Unit,
    onOpenProduct: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val orangeColor = Color(0xFFFF9800)

    // Автоматический скролл вниз при новых сообщениях
    val messages = (uiState as? ChatThreadUiState.Data)?.messages ?: emptyList()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(), // Чтобы UI поднимался при открытии клавиатуры
        topBar = {
            TopAppBar(
                title = {
                    val data = uiState as? ChatThreadUiState.Data
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            AsyncImage(
                                model = data?.sellerPhotoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(orangeColor.copy(alpha = 0.1f)),
                                contentScale = ContentScale.Crop,
                                error = null // Placeholder logic handled by background if needed
                            )

                            if (data?.isOnline == true) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(10.dp)
                                        .background(Color.White, CircleShape)
                                        .padding(1.5.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF4CAF50), CircleShape))
                                }
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Column {
                            Text(
                                text = data?.sellerName ?: "Чат",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (data?.isOnline == true) "В сети" else "Был(а) недавно",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (data?.isOnline == true) Color(0xFF4CAF50) else Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.MoreVert, null) }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Удалить чат") },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Вложения */ }) {
                        Icon(Icons.Default.Add, null, tint = orangeColor)
                    }

                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        placeholder = { Text("Сообщение…") },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    val canSend = input.trim().isNotEmpty()
                    IconButton(
                        onClick = {
                            if (canSend) {
                                onSend(input.trim())
                                input = ""
                            }
                        },
                        enabled = canSend,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (canSend) orangeColor else Color(0xFFE5E5E5))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            null,
                            tint = if (canSend) Color.White else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFBFBFB))
        ) {
            when (uiState) {
                is ChatThreadUiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color = orangeColor)
                }
                is ChatThreadUiState.Error -> {
                    Text(uiState.message, Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                }
                is ChatThreadUiState.Data -> {
                    Column(Modifier.fillMaxSize()) {
                        ProductContextBar(
                            title = uiState.productTitle,
                            price = uiState.productPrice,
                            imageUrl = uiState.productImageUrl,
                            onClick = onOpenProduct,
                            accentColor = orangeColor
                        )
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
}

@Composable
private fun ProductContextBar(
    title: String,
    price: Int,
    imageUrl: String?,
    onClick: () -> Unit,
    accentColor: Color
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text("$price ₽", style = MaterialTheme.typography.bodyMedium, color = accentColor, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
        }
    }
}

@Composable
private fun Bubble(msg: ChatMessageUi) {
    val isMine = msg.isMine

    // Определяем форму: у своих сообщений "хвостик" справа снизу, у чужих - слева снизу
    val shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isMine) 16.dp else 2.dp,
        bottomEnd = if (isMine) 2.dp else 16.dp
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(if (isMine) Color(0xFFFF9800) else Color(0xFFE9E9EB))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isMine) Color.White else Color.Black
                )
            }

            Row(
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = msg.timeText,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                if (isMine) {
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFF9800)
                    )
                }
            }
        }
    }
}
