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
    val avatarUrl: String = "",
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

    fun onAvatarUrlChange(v: String) {
        _state.value = _state.value.copy(avatarUrl = v)
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
                repo.updateMyProfile(
                    name = s.name.trim(),
                    address = s.address.trim(),
                    avatarUrl = s.avatarUrl.trim().ifBlank { null }
                )
            }
                .onSuccess {
                    _state.value = _state.value.copy(saving = false)
                    onSuccess()
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        saving = false,
                        error = it.message ?: "Не удалось сохранить"
                    )
                }
        }
    }
}
