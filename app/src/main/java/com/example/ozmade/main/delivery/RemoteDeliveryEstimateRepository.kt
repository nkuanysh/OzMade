package com.example.ozmade.main.delivery

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.DeliveryAddressRequest
import com.example.ozmade.network.model.DeliveryPackageRequest
import com.example.ozmade.network.model.IntercityEstimateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDeliveryEstimateRepository @Inject constructor(
    private val api: OzMadeApi
) : DeliveryEstimateRepository {

    override suspend fun estimateIntercityDelivery(
        fromAddress: DeliveryAddress,
        toAddress: DeliveryAddress,
        packageInfo: DeliveryPackageInfo
    ): Result<IntercityDeliveryEstimate> = withContext(Dispatchers.IO) {
        runCatching {
            val request = IntercityEstimateRequest(
                fromAddress = fromAddress.toRequest(),
                toAddress = toAddress.toRequest(),
                packageInfo = DeliveryPackageRequest(
                    weightGrams = packageInfo.weightGrams,
                    heightCm = packageInfo.heightCm,
                    widthCm = packageInfo.widthCm,
                    depthCm = packageInfo.depthCm
                ),
                tariffCodes = CDEK_PARCEL_TARIFF_CODES,
                deliveryMode = "DOOR_TO_DOOR"
            )

            val response = api.estimateIntercityDelivery(request)
            if (!response.isSuccessful) {
                error(mapEstimateError(response.code()))
            }

            val body = response.body() ?: error("Пустой ответ расчета доставки")
            IntercityDeliveryEstimate(
                provider = body.provider,
                price = body.price,
                currency = body.currency.ifBlank { "₸" },
                minDays = body.minDays,
                maxDays = body.maxDays,
                estimatedDateFrom = body.estimatedDateFrom,
                estimatedDateTo = body.estimatedDateTo
            )
        }
    }

    private fun DeliveryAddress.toRequest(): DeliveryAddressRequest {
        return DeliveryAddressRequest(
            city = city,
            fullAddress = fullAddress,
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun mapEstimateError(code: Int): String {
        return when (code) {
            400, 422 -> "Проверьте адрес отправления, адрес доставки и параметры товара."
            404 -> "Сервис расчета доставки пока недоступен."
            in 500..599 -> "Не удалось рассчитать доставку. Попробуйте позже или уточните у продавца."
            else -> "Не удалось рассчитать доставку (${code})."
        }
    }

    private companion object {
        // CDEK parcel tariffs: warehouse-warehouse, warehouse-door, door-warehouse, door-door.
        val CDEK_PARCEL_TARIFF_CODES = listOf(136, 137, 138, 139)
    }
}
