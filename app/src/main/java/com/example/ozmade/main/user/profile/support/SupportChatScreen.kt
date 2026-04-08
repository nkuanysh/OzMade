package com.example.ozmade.main.user.profile.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Модель сообщения для техподдержки
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
    messages: List<ChatMessageUi> = demoSupportMessages(),
    onSendMessage: (String) -> Unit = {}
) {
    var input by remember { mutableStateOf("") }
    val orangeAccent = Color(0xFFFF9800)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Служба поддержки", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            "Онлайн 8:00–22:00",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Напишите сообщение…", color = Color.Gray) },
                        singleLine = false,
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeAccent,
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedContainerColor = Color(0xFFFBFBFB),
                            unfocusedContainerColor = Color(0xFFFBFBFB)
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                    FloatingActionButton(
                        onClick = {
                            val text = input.trim()
                            if (text.isNotEmpty()) {
                                onSendMessage(text)
                                input = ""
                            }
                        },
                        containerColor = orangeAccent,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Отправить", modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        containerColor = Color(0xFFFBFBFB)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(msg, orangeAccent)
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessageUi, accentColor: Color) {
    val isMine = msg.isMine
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isMine) 18.dp else 2.dp,
                        bottomEnd = if (isMine) 2.dp else 18.dp
                    )
                )
                .background(if (isMine) accentColor else Color.White)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isMine) Color.White else Color(0xFF1A1A1A),
                lineHeight = 20.sp
            )
            Text(
                text = msg.timeText,
                style = MaterialTheme.typography.labelSmall,
                color = if (isMine) Color.White.copy(0.7f) else Color.Gray,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }
    }
}

private fun demoSupportMessages(): List<ChatMessageUi> = listOf(
    ChatMessageUi("1", "Здравствуйте! Чем можем помочь?", isMine = false, timeText = "09:12"),
    ChatMessageUi("2", "Привет! Хочу узнать про доставку.", isMine = true, timeText = "09:13"),
    ChatMessageUi("3", "Подскажите, по какому товару вопрос?", isMine = false, timeText = "09:14")
)
