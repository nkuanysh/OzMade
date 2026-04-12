package com.example.ozmade.main.user.profile.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.data.SellerLocalStore
import com.example.ozmade.main.user.profile.locale.AppLang
import com.example.ozmade.main.user.profile.locale.LanguageStore
import com.example.ozmade.network.auth.SessionStore
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository,
    private val sessionStore: SessionStore,
    private val firebaseAuth: FirebaseAuth,
    private val sellerLocalStore: SellerLocalStore,
    private val languageStore: LanguageStore
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)

    val currentLang: StateFlow<AppLang> = languageStore.langFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLang.RU)

    val uiState: StateFlow<ProfileUiState> = combine(repo.profileFlow, _error) { user, error ->
        when {
            user != null -> ProfileUiState.Data(user)
            error != null -> ProfileUiState.Error(error)
            else -> ProfileUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState.Loading)

    init {
        load()
    }

    fun load() {
        _error.value = null
        viewModelScope.launch {
            runCatching { repo.getMyProfile() }
                .onFailure {
                    if (repo.profileFlow.value == null) {
                        _error.value = it.message ?: "Ошибка загрузки профиля"
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            sessionStore.clear()
            sellerLocalStore.clear()
            languageStore.clear()
            firebaseAuth.signOut()
        }
    }

    fun setLanguage(lang: AppLang) {
        viewModelScope.launch {
            languageStore.setLang(lang)
        }
    }
}
