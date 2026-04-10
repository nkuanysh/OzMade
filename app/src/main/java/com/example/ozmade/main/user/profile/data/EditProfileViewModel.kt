package com.example.ozmade.main.user.profile.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileState(
    val loading: Boolean = true,
    val saving: Boolean = false,
    val name: String = "",
    val address: String = "",
    val addressLat: Double? = null,
    val addressLng: Double? = null,
    val avatarUrl: String = "",
    val selectedUri: android.net.Uri? = null,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.getMyProfile() }
                .onSuccess { user ->
                    _state.value = EditProfileState(
                        loading = false,
                        name = user.name,
                        address = user.address,
                        addressLat = user.addressLat,
                        addressLng = user.addressLng,
                        avatarUrl = user.avatarUrl.orEmpty()
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = it.message ?: "Не удалось загрузить профиль"
                    )
                }
        }
    }

    fun onNameChange(v: String) {
        _state.value = _state.value.copy(name = v)
    }

    fun onAddressChange(v: String) {
        _state.value = _state.value.copy(address = v)
    }

    fun onAddressPicked(address: String, lat: Double, lng: Double) {
        _state.value = _state.value.copy(
            address = address,
            addressLat = lat,
            addressLng = lng,
            error = null
        )
    }

    fun clearPickedAddress() {
        _state.value = _state.value.copy(
            address = "",
            addressLat = null,
            addressLng = null
        )
    }

    fun onAvatarUrlChange(v: String) {
        _state.value = _state.value.copy(avatarUrl = v)
    }

    fun onAvatarPicked(uri: android.net.Uri) {
        _state.value = _state.value.copy(selectedUri = uri)
    }

    fun save(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.name.trim().isEmpty()) {
            _state.value = s.copy(error = "Имя не должно быть пустым")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(saving = true, error = null)
            runCatching {
                val uploadedUrl = s.selectedUri?.let { uri ->
                    repo.uploadAvatar(uri)
                }

                val finalAvatarUrl = uploadedUrl ?: s.avatarUrl.trim().ifBlank { null }

                repo.updateMyProfile(
                    name = s.name.trim(),
                    address = s.address.trim(),
                    addressLat = s.addressLat,
                    addressLng = s.addressLng,
                    avatarUrl = finalAvatarUrl
                )
            }.onSuccess {
                _state.value = _state.value.copy(saving = false)
                onSuccess()
            }.onFailure {
                _state.value = _state.value.copy(
                    saving = false,
                    error = it.message ?: "Не удалось сохранить"
                )
            }
        }
    }
}