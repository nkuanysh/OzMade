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
        val profileDeferred = async { api.getSellerProfile() }
        val qualityDeferred = async { api.getSellerQuality() }

        val profileResp = profileDeferred.await()
        val qualityResp = qualityDeferred.await()

        if (!profileResp.isSuccessful) error("Не удалось загрузить профиль (${profileResp.code()})")
        
        val pDto = profileResp.body() ?: error("Пустой ответ профиля")
        val qDto = qualityResp.body() 

        SellerProfileUi(
            name = pDto.name,
            firstName = pDto.firstName,
            lastName = pDto.lastName,
            about = pDto.about,
            city = pDto.city,
            address = pDto.address,
            categories = pDto.categories,
            status = pDto.status,
            photoUrl = pDto.photoUrl,
            totalProducts = pDto.totalProducts,
            rating = qDto?.averageRating ?: pDto.rating ?: 0.0,
            ratingsCount = qDto?.ratingsCount ?: 0,
            ordersCount = qDto?.ordersCount ?: pDto.ordersCount ?: 0
        )
    }
}
