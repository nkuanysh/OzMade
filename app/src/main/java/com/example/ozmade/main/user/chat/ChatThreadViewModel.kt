package com.example.ozmade.main.user.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.user.chat.data.ChatMessageUi
import com.example.ozmade.main.user.chat.data.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        val productImageUrl: String?,
        val messages: List<ChatMessageUi>,
        val isOnline: Boolean = false
    ) : ChatThreadUiState()
}

@HiltViewModel
class ChatThreadViewModel @Inject constructor(
    private val repo: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatThreadUiState>(ChatThreadUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ChatEvent>()
    val events = _events.asSharedFlow()

    private var currentChatId: Int? = null
    private var pollingJob: Job? = null

    sealed class ChatEvent {
        object ChatDeleted : ChatEvent()
        data class ActionError(val message: String) : ChatEvent()
    }

    fun openChat(
        chatId: Int?,
        sellerId: Int,
        sellerName: String,
        productId: Int,
        productTitle: String,
        productPrice: Int,
        productImageUrl: String? = null
    ) {
        if (currentChatId == chatId && _uiState.value is ChatThreadUiState.Data) return

        currentChatId = chatId
        _uiState.value = ChatThreadUiState.Loading

        viewModelScope.launch {
            try {
                val msgs = if (chatId != null) {
                    repo.getMessages(chatId)
                } else {
                    emptyList()
                }

                val mockOnline = sellerId % 2 == 0

                _uiState.value = ChatThreadUiState.Data(
                    chatId = chatId,
                    productId = productId,
                    sellerName = sellerName,
                    productTitle = productTitle,
                    productPrice = productPrice,
                    productImageUrl = productImageUrl,
                    messages = msgs,
                    isOnline = mockOnline
                )

                if (chatId != null) startPolling(chatId)
            } catch (e: Exception) {
                _uiState.value = ChatThreadUiState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    private fun startPolling(chatId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(3000)
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
        val state = _uiState.value as? ChatThreadUiState.Data ?: return

        viewModelScope.launch {
            try {
                val newChatId = repo.sendMessageOrCreate(
                    productId = state.productId,
                    content = text,
                    existingChatId = state.chatId
                )

                if (state.chatId == null) {
                    currentChatId = newChatId
                    startPolling(newChatId)
                }

                val updatedMsgs = repo.getMessages(newChatId)

                _uiState.value = state.copy(
                    chatId = newChatId,
                    messages = updatedMsgs
                )
            } catch (e: Exception) {
                _events.emit(ChatEvent.ActionError("Не удалось отправить сообщение"))
            }
        }
    }

    fun deleteCurrentChat() {
        val chatId = currentChatId ?: return
        viewModelScope.launch {
            try {
                // Вызываем API для удаления (очистки истории для текущего пользователя)
                repo.deleteChat(chatId)
                _events.emit(ChatEvent.ChatDeleted)
            } catch (e: Exception) {
                _events.emit(ChatEvent.ActionError("Ошибка при очистке чата"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
