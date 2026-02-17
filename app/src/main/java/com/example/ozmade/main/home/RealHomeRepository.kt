package com.example.ozmade.main.home

import com.example.ozmade.network.api.HomeApi
import com.example.ozmade.network.dto.AdDto
import com.example.ozmade.network.dto.CategoryDto
import com.example.ozmade.network.dto.ProductDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RealHomeRepository @Inject constructor(
    private val api: HomeApi
) : HomeRepository {

    override suspend fun getHome(): HomeResponse = coroutineScope {
        // Параллельно: быстрее
        val adsDeferred = async { api.getAds() }
        val catsDeferred = async { api.getCategories() }
        val productsDeferred = async { api.getProducts() }

        val ads = adsDeferred.await().map { it.toDomain() }
        val categories = catsDeferred.await().map { it.toDomain() }
        val products = productsDeferred.await().map { it.toDomain() }

        HomeResponse(
            ads = ads,
            categories = categories,
            products = products
        )
    }
}

private fun AdDto.toDomain(): AdBanner =
    AdBanner(
        id = id,
        imageUrl = imageUrl,
        title = title,
        deeplink = deeplink
    )

private fun CategoryDto.toDomain(): Category =
    Category(
        id = id,
        title = title,
        iconUrl = iconUrl
    )

private fun ProductDto.toDomain(): Product =
    Product(
        id = id,
        title = title,
        price = price,
        city = city,
        address = address,
        rating = rating,
        imageUrl = imageUrl
    )
