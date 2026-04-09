package com.example.ozmade.main.seller.profile.data

import com.example.ozmade.network.api.OzMadeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealSellerProfileRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerProfileRepository {

    override suspend fun getSellerProfile(): SellerProfileUi = withContext(Dispatchers.IO) {
        // Fetch both profile and quality info to ensure we have "correct information" (stats)
        val profileDeferred = async { api.getSellerProfile() }
        val qualityDeferred = async { api.getSellerQuality() }

        val profileResp = profileDeferred.await()
        val qualityResp = qualityDeferred.await()

        if (!profileResp.isSuccessful) error("Не удалось загрузить профиль (${profileResp.code()})")
        
        val pDto = profileResp.body() ?: error("Пустой ответ профиля")
        val qDto = qualityResp.body() // quality might fail or be empty, handled gracefully

        SellerProfileUi(
            name = pDto.name,
            status = pDto.status,
            totalProducts = pDto.totalProducts,
            // Use quality DTO for rating and orders if available, as it's often more detailed
            rating = qDto?.averageRating ?: pDto.rating ?: 0.0,
            ratingsCount = qDto?.ratingsCount ?: 0,
            ordersCount = qDto?.ordersCount ?: pDto.ordersCount ?: 0
        )
    }
}