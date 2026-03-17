package com.example.ozmade.main.userHome.details.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.user.chat.data.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatActionState {
    data object Idle : ChatActionState()
    data object Loading : ChatActionState()
    data class Success(val chatId: Int, val productId: Int) : ChatActionState()
    data class Error(val message: String) : ChatActionState()
}

@HiltViewModel
class ProductChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatState = MutableStateFlow<ChatActionState>(ChatActionState.Idle)
    val chatState = _chatState.asStateFlow()

    fun initiateChat(productId: Int) {
        _chatState.value = ChatActionState.Loading

        viewModelScope.launch {
            runCatching {
                val existingChatId = chatRepository
                    .getThreads()
                    .firstOrNull { it.productId == productId }
                    ?.chatId

                if (existingChatId != null) {
                    existingChatId
                } else {
                    chatRepository.sendMessageOrCreate(
                        productId = productId,
                        content = "Заинтересован в этом товаре",
                        existingChatId = null
                    )
                }
            }
                .onSuccess { chatId ->
                    _chatState.value = ChatActionState.Success(chatId, productId)
                }
                .onFailure { error ->
                    _chatState.value = ChatActionState.Error(
                        error.message ?: "Не удалось открыть чат"
                    )
                }
        }
    }

    fun resetState() {
        _chatState.value = ChatActionState.Idle
    }
}