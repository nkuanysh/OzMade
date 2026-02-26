package com.example.ozmade.main.seller.profile.data

import com.example.ozmade.network.api.OzMadeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealSellerProfileRepository @Inject constructor(
    private val api: OzMadeApi
) : SellerProfileRepository {

    override suspend fun getSellerProfile(): SellerProfileUi = withContext(Dispatchers.IO) {
        val resp = api.getSellerProfile()
        if (!resp.isSuccessful) error("Не удалось загрузить профиль (${resp.code()})")
        val dto = resp.body() ?: error("Пустой ответ")

        SellerProfileUi(
            name = dto.name,
            status = dto.status,
            totalProducts = dto.totalProducts
        )
    }
}