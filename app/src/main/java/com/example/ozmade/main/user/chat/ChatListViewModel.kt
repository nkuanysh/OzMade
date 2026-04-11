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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatListUiState {
    data object Loading : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
    data class Data(
        val threads: List<ChatThreadUi>,
        val searchQuery: String = ""
    ) : ChatListUiState()
}

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repo: ChatRepository
) : ViewModel() {

    private val _threads = MutableStateFlow<List<ChatThreadUi>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        viewModelScope.launch {
            combine(_threads, _searchQuery, _isLoading, _errorMessage) { threads, query, loading, error ->
                when {
                    error != null -> ChatListUiState.Error(error)
                    loading && threads.isEmpty() -> ChatListUiState.Loading
                    else -> {
                        val filtered = if (query.isBlank()) {
                            threads
                        } else {
                            threads.filter {
                                it.sellerName.contains(query, ignoreCase = true) ||
                                        it.productTitle.contains(query, ignoreCase = true) ||
                                        it.lastMessage.contains(query, ignoreCase = true)
                            }
                        }
                        ChatListUiState.Data(filtered, query)
                    }
                }
            }.collect {
                _uiState.value = it
            }
        }
        load()
    }

    fun load() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            runCatching { repo.getThreads() }
                .onSuccess {
                    _threads.value = it
                    _isLoading.value = false
                    startPolling()
                }
                .onFailure {
                    _errorMessage.value = it.message ?: "Ошибка"
                    _isLoading.value = false
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearAll() {
        viewModelScope.launch {
            try {
                _threads.value.forEach { thread ->
                    repo.deleteChat(thread.chatId)
                }
                load()
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при очистке: ${e.message}"
            }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                runCatching { repo.getThreads() }
                    .onSuccess { threads ->
                        if (_threads.value != threads) {
                            _threads.value = threads
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
