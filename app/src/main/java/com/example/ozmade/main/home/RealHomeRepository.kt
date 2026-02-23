package com.example.ozmade.main.home

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.AdDto
import com.example.ozmade.network.model.CategoryDto
import com.example.ozmade.network.model.ProductDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RealHomeRepository @Inject constructor(
    private val api: OzMadeApi
) : HomeRepository {

    override suspend fun getHome(): HomeResponse = coroutineScope {
        val adsDeferred = async { api.getAds() }
        val catsDeferred = async { api.getCategories() }
        val trendingDeferred = async { api.getTrendingProducts() }
        val productsDeferred = async { api.getProducts() }

        val adsResponse = adsDeferred.await()
        val catsResponse = catsDeferred.await()
        val trendingResponse = trendingDeferred.await()
        val productsResponse = productsDeferred.await()

        val ads = adsResponse.body()?.map { it.toDomain() } ?: emptyList()
        val categories = catsResponse.body()?.map { it.toDomain() } ?: emptyList()
        val products = productsResponse.body()?.map { it.toDomain() } ?: emptyList()
        val trendingProducts = trendingResponse.body()?.map { it.toDomain() } ?: emptyList()

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
        id = id.toString(),
        title = title ?: name ?: "Unknown",
        price = cost ?: price ?: 0.0,
        city = address?.substringBefore(",") ?: "Unknown",
        address = address ?: "Unknown",
        rating = averageRating ?: 0.0,
        imageUrl = imageUrl ?:  "",
        categoryId = type ?: ""
    )