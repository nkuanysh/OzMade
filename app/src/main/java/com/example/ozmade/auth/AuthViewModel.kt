package com.example.ozmade.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.impl.FirebaseAuthRepository
import com.example.domain.entities.User
import com.example.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: FirebaseAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun requestOtp(activity: Activity, phone: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repo.requestOtp(activity, phone).collect { result ->
                result.fold(
                    onSuccess = { sess ->
                        // AUTO значит уже вошёл
                        if (sess.verificationId == "AUTO") _uiState.value = AuthUiState.Success
                        else _uiState.value = AuthUiState.OtpRequired(sess.phone, sess.verificationId)
                    },
                    onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "OTP request failed") }
                )
            }
        }
    }

    fun verifyOtp(verificationId: String, code: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repo.verifyOtp(verificationId, code).collect { result ->
                result.fold(
                    onSuccess = { _uiState.value = AuthUiState.Success },
                    onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Invalid code") }
                )
            }
        }
    }
}


sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class OtpRequired(val phone: String, val verificationId: String) : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

