package com.example.ozmade.main.seller.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.chat.data.SellerChatRepository
import com.example.ozmade.main.seller.chat.data.SellerChatThreadUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SellerChatListUiState {
    data object Loading : SellerChatListUiState()
    data class Error(val message: String) : SellerChatListUiState()
    data class Data(val threads: List<SellerChatThreadUi>) : SellerChatListUiState()
}

@HiltViewModel
class SellerChatListViewModel @Inject constructor(
    private val repo: SellerChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerChatListUiState>(SellerChatListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        load()
    }

    fun load() {
        _uiState.value = SellerChatListUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getThreads() }
                .onSuccess { 
                    _uiState.value = SellerChatListUiState.Data(it)
                    startPolling()
                }
                .onFailure { _uiState.value = SellerChatListUiState.Error(it.message ?: "Ошибка") }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                runCatching { repo.getThreads() }
                    .onSuccess { threads ->
                        val current = _uiState.value
                        if (current is SellerChatListUiState.Data && current.threads != threads) {
                            _uiState.value = SellerChatListUiState.Data(threads)
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