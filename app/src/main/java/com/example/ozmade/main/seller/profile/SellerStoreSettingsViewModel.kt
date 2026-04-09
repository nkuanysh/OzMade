package com.example.ozmade.main.seller.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.data.SellerRepository
import com.example.ozmade.network.model.UpdateSellerProfileRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SellerStoreSettingsUiState(
    val isLoading: Boolean = false,
    val storeName: String = "",
    val about: String = "",
    val city: String = "",
    val address: String = "",
    val logoUrl: String? = null,
    val localLogoUri: Uri? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class SellerStoreSettingsViewModel @Inject constructor(
    private val repository: SellerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerStoreSettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val profile = repository.getSellerProfile()
            if (profile != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        storeName = profile.name,
                        // Assuming status or some other field might be 'about' or we fetch more details
                        // For now let's use what we have or placeholder
                        about = "", 
                        logoUrl = null // If profile had a photo URL
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Не удалось загрузить данные") }
            }
        }
    }

    fun onStoreNameChange(name: String) = _uiState.update { it.copy(storeName = name) }
    fun onAboutChange(about: String) = _uiState.update { it.copy(about = about) }
    fun onCityChange(city: String) = _uiState.update { it.copy(city = city) }
    fun onAddressChange(address: String) = _uiState.update { it.copy(address = address) }
    fun onLogoSelected(uri: Uri) = _uiState.update { it.copy(localLogoUri = uri) }

    fun save() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            runCatching {
                var finalLogoUrl = currentState.logoUrl
                
                currentState.localLogoUri?.let { uri ->
                    finalLogoUrl = repository.uploadPhoto(uri).getOrThrow()
                }

                val request = UpdateSellerProfileRequest(
                    profile_picture = finalLogoUrl,
                    displayName = currentState.storeName,
                    about = currentState.about,
                    city = currentState.city,
                    address = currentState.address
                )
                
                repository.updateSellerProfile(request)
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Ошибка сохранения") }
            }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false) }
}
