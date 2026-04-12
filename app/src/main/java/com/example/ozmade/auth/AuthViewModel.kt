package com.example.ozmade.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.impl.FirebaseAuthRepository
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.FCMTokenRequest
import com.example.ozmade.network.model.SyncRequest
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: FirebaseAuthRepository,
    private val api: OzMadeApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private var authJob: Job? = null

    fun reset() {
        authJob?.cancel()
        _uiState.value = AuthUiState.Idle
    }

    fun requestOtp(activity: Activity, phone: String) {
        authJob?.cancel()
        _uiState.value = AuthUiState.Loading
        authJob = viewModelScope.launch {
            repo.requestOtp(activity, phone).collect { result ->
                result.fold(
                    onSuccess = { sess ->
                        if (sess.verificationId == "AUTO") {
                            _uiState.value = AuthUiState.Success
                            logFirebaseIdToken() // 👈 ВОТ СЮДА
                            sendFCMTokenToBackend(api)
                        } else {
                            _uiState.value = AuthUiState.OtpRequired(sess.phone, sess.verificationId)
                        }
                    },
                    onFailure = {
                        _uiState.value = AuthUiState.Error(it.message ?: "Ошибка запроса кода")
                    }
                )
            }
        }
    }

    fun verifyOtp(verificationId: String, code: String, phone: String) {
        authJob?.cancel()
        _uiState.value = AuthUiState.Loading
        authJob = viewModelScope.launch {
            repo.verifyOtp(verificationId, code).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = AuthUiState.Success
                        logFirebaseIdToken() // 👈 ВОТ СЮДА
                        sendFCMTokenToBackend(api)
                    },
                    onFailure = {
                        _uiState.value = AuthUiState.Error(
                            message = "Неверный код. Попробуйте еще раз",
                            phone = phone,
                            verificationId = verificationId
                        )
                    }
                )
            }
        }
    }

    private fun sendFCMTokenToBackend(api: OzMadeApi) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM", "Token error", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM", "Device token = $token")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = api.syncUser(SyncRequest(fcmToken = token))
                    if (response.isSuccessful) {
                        Log.d("FCM", "Token sent successfully via syncUser")
                    } else {
                        Log.e("FCM", "Token sync failed: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("FCM", "Sync error", e)
                }
            }
        }
    }
    private fun logFirebaseIdToken() {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Log.e("FIREBASE_ID_TOKEN", "User is null")
            return
        }

        user.getIdToken(true)
            .addOnSuccessListener { result ->
                val idToken = result.token
                Log.d("FIREBASE_ID_TOKEN", idToken ?: "null")
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_ID_TOKEN", "Failed to get token", e)
            }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class OtpRequired(val phone: String, val verificationId: String) : AuthUiState()
    object Success : AuthUiState()
    data class Error(
        val message: String,
        val phone: String? = null,
        val verificationId: String? = null
    ) : AuthUiState()
}
