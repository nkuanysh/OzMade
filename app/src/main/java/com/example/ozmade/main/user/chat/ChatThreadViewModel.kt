package com.example.ozmade.main.user.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.user.chat.data.ChatMessageUi
import com.example.ozmade.main.user.chat.data.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatThreadUiState {
    data object Loading : ChatThreadUiState()
    data class Error(val message: String) : ChatThreadUiState()
    data class Data(
        val chatId: Int?,
        val productId: Int,
        val sellerName: String,
        val productTitle: String,
        val productPrice: Int,
        val messages: List<ChatMessageUi>
    ) : ChatThreadUiState()
}

@HiltViewModel
class ChatThreadViewModel @Inject constructor(
    private val repo: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatThreadUiState>(ChatThreadUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var currentChatId: Int? = null
    private var sellerName: String = ""
    private var productTitle: String = ""
    private var productPrice: Int = 0

    fun openChat(
        sellerId: String,
        sellerName: String,
        productId: String,
        productTitle: String,
        productPrice: Int,
        productImageUrl: String? = null
    ) {
        _uiState.value = ChatThreadUiState.Loading
        viewModelScope.launch {
            runCatching {
                val pid = productId.toIntOrNull() ?: error("Некорректный productId: $productId")

                val existingChatId = repo.findChatIdOrNull(pid)

                this@ChatThreadViewModel.sellerName = sellerName
                this@ChatThreadViewModel.productTitle = productTitle
                this@ChatThreadViewModel.productPrice = productPrice
                currentChatId = existingChatId

                val msgs = if (existingChatId != null) repo.getMessages(existingChatId) else emptyList()

                ChatThreadUiState.Data(
                    chatId = existingChatId,
                    productId = pid,
                    sellerName = sellerName,
                    productTitle = productTitle,
                    productPrice = productPrice,
                    messages = msgs
                )
            }.onSuccess { _uiState.value = it }
                .onFailure { _uiState.value = ChatThreadUiState.Error(it.message ?: "Ошибка") }
        }
    }

    fun send(text: String) {
        val state = _uiState.value
        if (state !is ChatThreadUiState.Data) return

        viewModelScope.launch {
            runCatching {
                val newChatId = repo.sendMessageOrCreate(
                    productId = state.productId,
                    content = text,
                    existingChatId = state.chatId
                )
                currentChatId = newChatId

                val msgs = repo.getMessages(newChatId)
                state.copy(chatId = newChatId, messages = msgs)
            }.onSuccess { _uiState.value = it }
                .onFailure { _uiState.value = ChatThreadUiState.Error(it.message ?: "Ошибка отправки") }
        }
    }
}
