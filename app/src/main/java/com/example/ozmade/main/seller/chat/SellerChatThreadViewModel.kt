package com.example.ozmade.main.seller.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.chat.data.SellerChatMessageUi
import com.example.ozmade.main.seller.chat.data.SellerChatRepository
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

sealed class SellerChatThreadUiState {
    data object Loading : SellerChatThreadUiState()
    data class Error(val message: String) : SellerChatThreadUiState()
    data class Data(
        val chatId: Int,
        val buyerName: String,
        val buyerPhotoUrl: String? = null,
        val productId: Int = 0,
        val productTitle: String = "",
        val productImageUrl: String? = null,
        val messages: List<SellerChatMessageUi>
    ) : SellerChatThreadUiState()
}

@HiltViewModel
class SellerChatThreadViewModel @Inject constructor(
    private val repo: SellerChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerChatThreadUiState>(SellerChatThreadUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SellerChatEvent>()
    val events = _events.asSharedFlow()

    private var currentChatId: Int? = null
    private var buyerName: String = ""
    private var buyerPhotoUrl: String? = null
    private var pollingJob: Job? = null

    sealed class SellerChatEvent {
        data object ChatDeleted : SellerChatEvent()
        data class ActionError(val message: String) : SellerChatEvent()
    }

    fun open(chatId: Int, buyerName: String, buyerPhotoUrl: String? = null) {
        this.currentChatId = chatId
        this.buyerName = buyerName
        this.buyerPhotoUrl = buyerPhotoUrl

        _uiState.value = SellerChatThreadUiState.Loading
        viewModelScope.launch {
            runCatching { 
                val msgs = repo.getMessages(chatId)
                // Get thread info from repo to populate product details
                val threads = repo.getThreads()
                val thread = threads.find { it.chatId == chatId }
                msgs to thread
            }
                .onSuccess { (msgs, thread) ->
                    _uiState.value = SellerChatThreadUiState.Data(
                        chatId = chatId,
                        buyerName = buyerName,
                        buyerPhotoUrl = buyerPhotoUrl,
                        productId = thread?.productId ?: 0,
                        productTitle = thread?.productTitle ?: "",
                        productImageUrl = thread?.productImageUrl,
                        messages = msgs
                    )
                    startPolling(chatId)
                }
                .onFailure {
                    _uiState.value = SellerChatThreadUiState.Error(it.message ?: "Ошибка")
                }
        }
    }

    private fun startPolling(chatId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(3000) // Poll every 3 seconds
                val state = _uiState.value
                if (state is SellerChatThreadUiState.Data) {
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
        val chatId = currentChatId ?: return
        viewModelScope.launch {
            runCatching {
                repo.sendMessage(chatId, text)
                repo.getMessages(chatId)
            }.onSuccess { msgs ->
                val state = _uiState.value
                if (state is SellerChatThreadUiState.Data) {
                    _uiState.value = state.copy(messages = msgs)
                }
            }.onFailure {
                _events.emit(SellerChatEvent.ActionError(it.message ?: "Ошибка при отправке"))
            }
        }
    }

    fun deleteChat() {
        val chatId = currentChatId ?: return
        viewModelScope.launch {
            runCatching { repo.deleteChat(chatId) }
                .onSuccess { _events.emit(SellerChatEvent.ChatDeleted) }
                .onFailure { _events.emit(SellerChatEvent.ActionError(it.message ?: "Ошибка при удалении")) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
