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
        val threadId: String,
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

    private var currentThreadId: String? = null
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
                val tid = repo.ensureThread(
                    sellerId, sellerName, productId, productTitle, productPrice, productImageUrl
                )
                currentThreadId = tid
                this@ChatThreadViewModel.sellerName = sellerName
                this@ChatThreadViewModel.productTitle = productTitle
                this@ChatThreadViewModel.productPrice = productPrice
                val msgs = repo.getMessages(tid)
                tid to msgs
            }.onSuccess { (tid, msgs) ->
                _uiState.value = ChatThreadUiState.Data(
                    threadId = tid,
                    sellerName = sellerName,
                    productTitle = productTitle,
                    productPrice = productPrice,
                    messages = msgs
                )
            }.onFailure {
                _uiState.value = ChatThreadUiState.Error(it.message ?: "Ошибка")
            }
        }
    }

    fun send(text: String) {
        val tid = currentThreadId ?: return
        viewModelScope.launch {
            repo.sendMessage(tid, text)
            val msgs = repo.getMessages(tid)
            _uiState.value = ChatThreadUiState.Data(
                threadId = tid,
                sellerName = sellerName,
                productTitle = productTitle,
                productPrice = productPrice,
                messages = msgs
            )
        }
    }
}
