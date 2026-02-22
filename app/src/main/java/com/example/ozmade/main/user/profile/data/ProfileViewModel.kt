package com.example.ozmade.main.user.profile.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getMyProfile() }
                .onSuccess { _uiState.value = ProfileUiState.Data(it) }
                .onFailure { _uiState.value =
                    ProfileUiState.Error(it.message ?: "Ошибка загрузки профиля")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            FirebaseAuth.getInstance().signOut()}
    }
}
