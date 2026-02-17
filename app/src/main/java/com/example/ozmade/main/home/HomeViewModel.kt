package com.example.ozmade.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            runCatching { repo.getHome() }
                .onSuccess { resp ->
                    _uiState.value = HomeUiState.Data(
                        ads = resp.ads,
                        categories = resp.categories,
                        products = resp.products
                    )
                }
                .onFailure {
                    _uiState.value = HomeUiState.Error(it.message ?: "Ошибка загрузки Home")
                }
        }
    }
}
