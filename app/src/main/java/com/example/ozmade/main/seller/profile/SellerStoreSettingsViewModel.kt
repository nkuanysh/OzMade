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
    val firstName: String = "",
    val lastName: String = "",
    val storeName: String = "",
    val about: String = "",
    val city: String = "",
    val address: String = "",
    val selectedCategories: List<String> = emptyList(),
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
                        firstName = profile.firstName ?: "",
                        lastName = profile.lastName ?: "",
                        storeName = profile.name,
                        about = profile.about ?: "",
                        city = profile.city ?: "",
                        address = profile.address ?: "",
                        selectedCategories = profile.categories ?: emptyList(),
                        logoUrl = profile.photoUrl
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Не удалось загрузить данные") }
            }
        }
    }

    fun onFirstNameChange(name: String) = _uiState.update { it.copy(firstName = name) }
    fun onLastNameChange(name: String) = _uiState.update { it.copy(lastName = name) }
    fun onStoreNameChange(name: String) = _uiState.update { it.copy(storeName = name) }
    fun onAboutChange(about: String) = _uiState.update { it.copy(about = about) }
    fun onCityChange(city: String) = _uiState.update { it.copy(city = city) }
    fun onAddressChange(address: String) = _uiState.update { it.copy(address = address) }
    fun onLogoSelected(uri: Uri) = _uiState.update { it.copy(localLogoUri = uri) }
    
    fun onCategoryToggle(category: String) {
        _uiState.update { state ->
            val newList = if (state.selectedCategories.contains(category)) {
                state.selectedCategories - category
            } else {
                state.selectedCategories + category
            }
            state.copy(selectedCategories = newList)
        }
    }

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
                    avatarUrl = finalLogoUrl,
                    name = currentState.storeName,
                    displayName = currentState.storeName,
                    about = currentState.about,
                    city = currentState.city,
                    address = currentState.address,
                    firstName = currentState.firstName,
                    lastName = currentState.lastName,
                    categories = currentState.selectedCategories
                )
                
                repository.updateSellerProfile(request).getOrThrow()
            }.onSuccess {
                // После успешного сохранения перезагружаем профиль, чтобы данные в UI обновились точно
                loadProfile()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Ошибка сохранения") }
            }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false) }
}
