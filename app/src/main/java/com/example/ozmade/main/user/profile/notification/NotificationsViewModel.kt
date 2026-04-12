package com.example.ozmade.main.user.profile.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NotificationsUiState {
    object Loading : NotificationsUiState()
    data class Data(val items: List<NotificationItem>) : NotificationsUiState()
    data class Error(val message: String) : NotificationsUiState()
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationsUiState.Loading
            try {
                val items = repository.getNotifications()
                _uiState.value = NotificationsUiState.Data(items)
            } catch (e: Exception) {
                _uiState.value = NotificationsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                repository.markAllAsRead()
                loadNotifications()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun clearAll() {
        // Backend doesn't have "delete all", maybe mark all as read is enough or we just clear locally if needed
        // For now let's just mark all as read
        markAllAsRead()
    }
}
