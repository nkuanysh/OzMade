package com.example.ozmade.main.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.chat.data.ChatRepository
import com.example.ozmade.main.chat.data.ChatThreadUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatListUiState {
    data object Loading : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
    data class Data(val threads: List<ChatThreadUi>) : ChatListUiState()
}

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repo: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun load() {
        _uiState.value = ChatListUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getThreads() }
                .onSuccess { _uiState.value = ChatListUiState.Data(it) }
                .onFailure { _uiState.value = ChatListUiState.Error(it.message ?: "Ошибка") }
        }
    }
}
