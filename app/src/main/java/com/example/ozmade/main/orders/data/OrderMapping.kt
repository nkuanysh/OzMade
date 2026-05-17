package com.example.ozmade.main.orders.data

import com.example.ozmade.main.delivery.DEFAULT_HEIGHT_CM
import com.example.ozmade.main.delivery.DEFAULT_LENGTH_CM
import com.example.ozmade.main.delivery.DEFAULT_WEIGHT_GRAMS
import com.example.ozmade.main.delivery.DEFAULT_WIDTH_CM
import com.example.ozmade.main.delivery.extractCity
import com.example.ozmade.network.model.IntercityDeliveryOrderDto

fun IntercityDeliveryOrderDto?.toOrderUi(): IntercityDeliveryOrderUi? {
    val dto = this ?: return null
    return IntercityDeliveryOrderUi(
        provider = dto.provider ?: "CDEK",
        price = dto.price ?: 0.0,
        currency = dto.currency?.ifBlank { "₸" } ?: "₸",
        minDays = dto.minDays ?: 0,
        maxDays = dto.maxDays ?: 0,
        estimatedDateFrom = dto.estimatedDateFrom.orEmpty(),
        estimatedDateTo = dto.estimatedDateTo.orEmpty(),
        fromCity = dto.fromAddress?.city
            ?: extractCity(dto.fromAddress?.fullAddress).orEmpty(),
        fromAddress = dto.fromAddress?.fullAddress.orEmpty(),
        fromLat = dto.fromAddress?.latitude,
        fromLng = dto.fromAddress?.longitude,
        toCity = dto.toAddress?.city
            ?: extractCity(dto.toAddress?.fullAddress).orEmpty(),
        toAddress = dto.toAddress?.fullAddress
            ?: dto.receiverAddress.orEmpty(),
        toLat = dto.toAddress?.latitude,
        toLng = dto.toAddress?.longitude,
        receiverName = dto.receiverName.orEmpty(),
        receiverPhone = dto.receiverPhone.orEmpty(),
        weightGrams = dto.packageInfo?.weightGrams ?: DEFAULT_WEIGHT_GRAMS,
        heightCm = dto.packageInfo?.heightCm ?: DEFAULT_HEIGHT_CM,
        widthCm = dto.packageInfo?.widthCm ?: DEFAULT_WIDTH_CM,
        depthCm = dto.packageInfo?.depthCm ?: DEFAULT_LENGTH_CM,
        comment = dto.comment
    )
}
