package com.example.ozmade.main.profile.data

data class UserProfile(
    val id: String,
    val name: String,
    val phone: String,
    val avatarUrl: String? = null,
    val address: String = ""
)


sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Data(val user: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
