package com.example.ozmade.main.user.profile.data

data class UserProfile(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String = "",
    val addressLat: Double? = null,
    val addressLng: Double? = null,
    val photoUrl: String? = null
)


sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Data(val user: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
