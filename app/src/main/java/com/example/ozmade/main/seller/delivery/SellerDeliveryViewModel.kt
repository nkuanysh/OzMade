package com.example.ozmade.main.seller.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.UpdateSellerDeliveryRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerDeliveryViewModel @Inject constructor(
    private val api: OzMadeApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerDeliveryUiState>(SellerDeliveryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var saved: SellerDeliveryUi? = null

    fun load() {
        _uiState.value = SellerDeliveryUiState.Loading
        viewModelScope.launch {
            runCatching {
                val resp = api.getSellerDelivery()
                if (!resp.isSuccessful) error("Не удалось загрузить доставку (${resp.code()})")
                val d = resp.body() ?: error("Пустой ответ")

                SellerDeliveryUi(
                    pickupEnabled = d.pickupEnabled,
                    pickupAddress = d.pickupAddress.orEmpty(),
                    pickupTime = d.pickupTime.orEmpty(),

                    myDeliveryEnabled = d.myDeliveryEnabled,
                    centerLat = d.centerLat?.toString().orEmpty(),
                    centerLng = d.centerLng?.toString().orEmpty(),
                    radiusKm = d.radiusKm ?: 3,
                    centerAddress = d.centerAddress.orEmpty(),

                    intercityEnabled = d.intercityEnabled
                )
            }.onSuccess {
                saved = it
                _uiState.value = SellerDeliveryUiState.Data(it)
            }.onFailure {
                _uiState.value = SellerDeliveryUiState.Error(it.message ?: "Ошибка")
            }
        }
    }

    fun updateLocal(block: (SellerDeliveryUi) -> SellerDeliveryUi) {
        val cur = (_uiState.value as? SellerDeliveryUiState.Data)?.ui ?: return
        _uiState.value = SellerDeliveryUiState.Data(block(cur))
    }

    fun saveAll(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cur = (_uiState.value as? SellerDeliveryUiState.Data)?.ui ?: return
        viewModelScope.launch {
            runCatching {
                val lat = cur.centerLat.trim().toDoubleOrNull()
                val lng = cur.centerLng.trim().toDoubleOrNull()

                if (cur.myDeliveryEnabled && (lat == null || lng == null)) {
                    error("Укажи координаты точки (lat/lng)")
                }

                val req = UpdateSellerDeliveryRequest(
                    pickupEnabled = cur.pickupEnabled,
                    pickupAddress = cur.pickupAddress.trim().ifBlank { null },
                    pickupTime = cur.pickupTime.trim().ifBlank { null },
                    myDeliveryEnabled = cur.myDeliveryEnabled,
                    centerLat = lat,
                    centerLng = lng,
                    radiusKm = cur.radiusKm,
                    centerAddress = cur.centerAddress.trim().ifBlank { null },
                    intercityEnabled = cur.intercityEnabled
                )
                val resp = api.updateSellerDelivery(req)
                if (!resp.isSuccessful) error("Не удалось сохранить (${resp.code()})")
            }.onSuccess {
                saved = cur
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Ошибка")
            }
        }
    }
}
