package com.example.ozmade.main.seller.registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.data.SellerRepository
import com.example.ozmade.network.model.SellerRegistrationRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SellerRegState {
    object Idle : SellerRegState()
    object Loading : SellerRegState()
    object Success : SellerRegState()
    data class Error(val message: String) : SellerRegState()
}

@HiltViewModel
class SellerRegistrationViewModel @Inject constructor(
    private val repo: SellerRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SellerRegState>(SellerRegState.Idle)
    val state = _state.asStateFlow()

    private val _selectedUri = MutableStateFlow<Uri?>(null)
    val selectedUri = _selectedUri.asStateFlow()

    fun onImageSelected(uri: Uri?) {
        _selectedUri.value = uri
    }

    fun submit(
        firstName: String,
        lastName: String,
        displayName: String,
        city: String,
        address: String,
        categories: List<String>,
        about: String?
    ) {
        _state.value = SellerRegState.Loading
        viewModelScope.launch {
            try {
                var photoUrl: String? = null
                _selectedUri.value?.let { uri ->
                    photoUrl = repo.uploadPhoto(uri).getOrNull()
                }

                val request = SellerRegistrationRequestDto(
                    firstName = firstName,
                    lastName = lastName,
                    displayName = displayName,
                    city = city,
                    address = address,
                    categories = categories,
                    about = about,
                    idCardUrl = photoUrl // Using idCardUrl for the profile photo as per current DTO, or we might need to adjust
                )

                repo.registerSeller(request)
                    .onSuccess {
                        _state.value = SellerRegState.Success
                    }
                    .onFailure {
                        _state.value = SellerRegState.Error(it.message ?: "Ошибка регистрации")
                    }
            } catch (e: Exception) {
                _state.value = SellerRegState.Error(e.message ?: "Произошла ошибка")
            }
        }
    }

    fun reset() {
        _state.value = SellerRegState.Idle
    }
}