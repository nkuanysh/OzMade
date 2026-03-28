package com.example.ozmade.main.user.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.user.chat.data.ChatMessageUi
import com.example.ozmade.main.user.chat.data.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
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
    private var pollingJob: Job? = null

    fun openChat(
        chatId: Int?,
        sellerId: Int,
        sellerName: String,
        productId: Int,
        productTitle: String,
        productPrice: Int,
        productImageUrl: String? = null
    ) {
        currentChatId = chatId
        _uiState.value = ChatThreadUiState.Loading

        viewModelScope.launch {
            runCatching {
                val msgs = if (chatId != null) {
                    repo.getMessages(chatId)
                } else {
                    emptyList()
                }

                ChatThreadUiState.Data(
                    chatId = chatId,
                    productId = productId,
                    sellerName = sellerName,
                    productTitle = productTitle,
                    productPrice = productPrice,
                    messages = msgs
                )
            }.onSuccess { 
                _uiState.value = it 
                if (chatId != null) startPolling(chatId)
            }.onFailure { 
                _uiState.value = ChatThreadUiState.Error(it.message ?: "Ошибка") 
            }
        }
    }

    private fun startPolling(chatId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(3000) // Poll every 3 seconds
                val state = _uiState.value
                if (state is ChatThreadUiState.Data) {
                    runCatching { repo.getMessages(chatId) }
                        .onSuccess { newMsgs ->
                            if (newMsgs != state.messages) {
                                _uiState.value = state.copy(messages = newMsgs)
                            }
                        }
                }
            }
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

                if (state.chatId == null) {
                    currentChatId = newChatId
                    startPolling(newChatId)
                }

                val msgs = repo.getMessages(newChatId)

                state.copy(
                    chatId = newChatId,
                    messages = msgs
                )
            }.onSuccess { _uiState.value = it }
                .onFailure { _uiState.value = ChatThreadUiState.Error(it.message ?: "Ошибка отправки") }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}