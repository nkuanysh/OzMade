package com.example.ozmade.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.User
import com.example.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onLogin(phone: String, pass: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.login(phone, pass).collect { result ->
                result.fold(
                    onSuccess = { _uiState.value = AuthUiState.Success(it) },
                    onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Login failed") }
                )
            }
        }
    }

    fun onRegisterBuyer(phone: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.registerBuyer(phone).collect { result ->
                result.fold(
                    onSuccess = { _uiState.value = AuthUiState.OtpRequired(phone) },
                    onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Registration failed") }
                )
            }
        }
    }

    fun onVerifyOtp(phone: String, otp: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.verifyOtp(phone, otp).collect { result ->
                result.fold(
                    onSuccess = { _uiState.value = AuthUiState.Success(it) },
                    onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Invalid OTP") }
                )
            }
        }
    }

    fun onGoogleSignIn() {
        _uiState.value = AuthUiState.GoogleSignIn
    }

    fun onGoogleSignInResult(idToken: String?) {
        if (idToken == null) {
            _uiState.value = AuthUiState.Error("Google Sign in failed")
            return
        }
        viewModelScope.launch {
            authRepository.googleLogin(idToken).collect { result ->
                result.fold(
                    onSuccess = { _uiState.value = AuthUiState.Success(it) },
                    onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Google Sign in failed") }
                )
            }
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object GoogleSignIn : AuthUiState()
    data class OtpRequired(val phone: String) : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
