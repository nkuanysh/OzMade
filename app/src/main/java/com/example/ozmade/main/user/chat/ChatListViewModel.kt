package com.example.ozmade.main.user.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.user.chat.data.ChatRepository
import com.example.ozmade.main.user.chat.data.ChatThreadUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
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

    private var pollingJob: Job? = null

    init {
        load()
    }

    fun load() {
        _uiState.value = ChatListUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getThreads() }
                .onSuccess { 
                    _uiState.value = ChatListUiState.Data(it)
                    startPolling()
                }
                .onFailure { _uiState.value = ChatListUiState.Error(it.message ?: "Ошибка") }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(5000) // Poll every 5 seconds for the list
                runCatching { repo.getThreads() }
                    .onSuccess { threads ->
                        val current = _uiState.value
                        if (current is ChatListUiState.Data && current.threads != threads) {
                            _uiState.value = ChatListUiState.Data(threads)
                        }
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
