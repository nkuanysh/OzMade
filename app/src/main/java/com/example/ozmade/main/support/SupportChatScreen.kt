package com.example.ozmade.main.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// ✅ модель сообщения (готово для бэкенда)
data class ChatMessageUi(
    val id: String,
    val text: String,
    val isMine: Boolean,
    val timeText: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportChatScreen(
    onBack: () -> Unit,
    // ✅ когда будет бэкенд: прокинешь реальные сообщения
    messages: List<ChatMessageUi> = demoSupportMessages(),
    // ✅ когда будет бэкенд: отправка будет дергать POST /support/messages
    onSendMessage: (String) -> Unit = {}
) {
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Служба поддержки", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            "Онлайн 8:00–22:00",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Напишите сообщение…") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    IconButton(
                        onClick = {
                            val text = input.trim()
                            if (text.isNotEmpty()) {
                                onSendMessage(text)
                                input = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Отправить")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(msg)
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessageUi) {
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
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodyLarge
            )
            if (msg.timeText.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = msg.timeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ✅ временные сообщения-заглушки
private fun demoSupportMessages(): List<ChatMessageUi> = listOf(
    ChatMessageUi("1", "Здравствуйте! Чем можем помочь?", isMine = false, timeText = "09:12"),
    ChatMessageUi("2", "Привет! Хочу узнать про доставку.", isMine = true, timeText = "09:13"),
    ChatMessageUi("3", "Подскажите, по какому товару вопрос?", isMine = false, timeText = "09:14")
)
