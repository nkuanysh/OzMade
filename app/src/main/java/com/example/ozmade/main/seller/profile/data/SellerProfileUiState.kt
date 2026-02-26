package com.example.ozmade.main.seller.profile.data

sealed class SellerProfileUiState {
    data object Loading : SellerProfileUiState()
    data class Error(val message: String) : SellerProfileUiState()
    data class Data(val profile: SellerProfileUi) : SellerProfileUiState()
}