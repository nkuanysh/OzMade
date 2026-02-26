package com.example.ozmade.main.seller.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ozmade.main.seller.chat.data.SellerChatThreadUi

@Composable
fun SellerChatScreen(
    onOpenChat: (SellerChatThreadUi) -> Unit,
    viewModel: SellerChatListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Чаты", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        when (val state = uiState) {
            is SellerChatListUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SellerChatListUiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.load() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Повторить")
                }
            }
            is SellerChatListUiState.Data -> {
                if (state.threads.isEmpty()) {
                    Text(
                        "Пока нет чатов с покупателями 🙂",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        state.threads.forEach { t ->
                            SellerThreadCard(thread = t, onClick = { onOpenChat(t) })
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun SellerThreadCard(thread: SellerChatThreadUi, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(thread.buyerName, style = MaterialTheme.typography.titleMedium)
                Text(
                    thread.lastMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                thread.lastTimeText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}