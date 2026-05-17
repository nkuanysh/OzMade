package com.example.ozmade.main.delivery

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockDeliveryEstimateRepository @Inject constructor() : DeliveryEstimateRepository {

    override suspend fun estimateIntercityDelivery(
        fromAddress: DeliveryAddress,
        toAddress: DeliveryAddress,
        packageInfo: DeliveryPackageInfo
    ): Result<IntercityDeliveryEstimate> = withContext(Dispatchers.Default) {
        runCatching {
            val normalizedFrom = fromAddress.city.normalizeCity()
            val normalizedTo = toAddress.city.normalizeCity()

            require(normalizedFrom.isNotBlank()) { "Укажите город отправления" }
            require(normalizedTo.isNotBlank()) { "Укажите город получения" }
            require(!normalizedFrom.equals(normalizedTo, ignoreCase = true)) {
                "Для этого города лучше выбрать самовывоз или мою доставку."
            }

            delay(250)

            val route = normalizedFrom to normalizedTo
            val (basePrice, minDays, maxDays) = when (route) {
                "алматы" to "астана" -> Triple(3500, 2, 4)
                "астана" to "алматы" -> Triple(3500, 2, 4)
                "алматы" to "шымкент" -> Triple(3000, 2, 3)
                else -> Triple(4250, 3, 6)
            }

            val safeWeight = packageInfo.weightGrams
            val safeLength = packageInfo.depthCm
            val safeWidth = packageInfo.widthCm
            val safeHeight = packageInfo.heightCm
            val volumeWeight = (safeLength * safeWidth * safeHeight) / 5000.0
            val realWeight = safeWeight / 1000.0
            val packageMultiplier = maxOf(realWeight, volumeWeight).coerceAtLeast(1.0)
            val packageSurcharge = ((packageMultiplier - 1.0) * 350).toInt()

            val today = LocalDate.now()
            IntercityDeliveryEstimate(
                provider = "CDEK",
                price = (basePrice + packageSurcharge).toDouble(),
                minDays = minDays,
                maxDays = maxDays,
                estimatedDateFrom = today.plusDays(minDays.toLong()).toString(),
                estimatedDateTo = today.plusDays(maxDays.toLong()).toString()
            )
        }
    }
}

private fun String.normalizeCity(): String = trim()
    .lowercase()
    .replace("ё", "е")
