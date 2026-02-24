package com.example.ozmade.main.seller.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.chat.data.SellerChatRepository
import com.example.ozmade.main.seller.chat.data.SellerChatThreadUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun load() {
        _uiState.value = SellerChatListUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getThreads() }
                .onSuccess { _uiState.value = SellerChatListUiState.Data(it) }
                .onFailure { _uiState.value = SellerChatListUiState.Error(it.message ?: "Ошибка") }
        }
    }
}