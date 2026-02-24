package com.example.ozmade.main.seller.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.chat.data.SellerChatMessageUi
import com.example.ozmade.main.seller.chat.data.SellerChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SellerChatThreadUiState {
    data object Loading : SellerChatThreadUiState()
    data class Error(val message: String) : SellerChatThreadUiState()
    data class Data(
        val chatId: Int,
        val buyerName: String,
        val messages: List<SellerChatMessageUi>
    ) : SellerChatThreadUiState()
}

@HiltViewModel
class SellerChatThreadViewModel @Inject constructor(
    private val repo: SellerChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerChatThreadUiState>(SellerChatThreadUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var currentChatId: Int? = null
    private var buyerName: String = ""

    fun open(chatId: Int, buyerName: String) {
        this.currentChatId = chatId
        this.buyerName = buyerName

        _uiState.value = SellerChatThreadUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getMessages(chatId) }
                .onSuccess { msgs ->
                    _uiState.value = SellerChatThreadUiState.Data(chatId, buyerName, msgs)
                }
                .onFailure {
                    _uiState.value = SellerChatThreadUiState.Error(it.message ?: "Ошибка")
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
                _uiState.value = SellerChatThreadUiState.Data(chatId, buyerName, msgs)
            }.onFailure {
                _uiState.value = SellerChatThreadUiState.Error(it.message ?: "Ошибка")
            }
        }
    }
}