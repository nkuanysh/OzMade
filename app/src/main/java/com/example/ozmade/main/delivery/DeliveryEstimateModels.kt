package com.example.ozmade.main.delivery

data class DeliveryAddress(
    val city: String,
    val fullAddress: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class DeliveryPackageInfo(
    val weightGrams: Int = DEFAULT_WEIGHT_GRAMS,
    val heightCm: Int = DEFAULT_HEIGHT_CM,
    val widthCm: Int = DEFAULT_WIDTH_CM,
    val depthCm: Int = DEFAULT_LENGTH_CM
)

data class IntercityDeliveryEstimate(
    val provider: String,
    val price: Int,
    val minDays: Int,
    val maxDays: Int,
    val estimatedDateFrom: String,
    val estimatedDateTo: String,
    val currency: String = "₸"
)

interface DeliveryEstimateRepository {
    suspend fun estimateIntercityDelivery(
        fromCity: String,
        toCity: String,
        weightGrams: Int?,
        lengthCm: Int?,
        widthCm: Int?,
        heightCm: Int?
    ): Result<IntercityDeliveryEstimate>
}

const val DEFAULT_WEIGHT_GRAMS = 1000
const val DEFAULT_LENGTH_CM = 30
const val DEFAULT_WIDTH_CM = 20
const val DEFAULT_HEIGHT_CM = 15
