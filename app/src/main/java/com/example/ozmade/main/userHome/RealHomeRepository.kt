package com.example.ozmade.main.userHome

import android.util.Log
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
        val productsDeferred = async { api.getProducts() }
        val adsDeferred = async { runCatching { api.getAds() }.getOrNull() }
        val catsDeferred = async { runCatching { api.getCategories() }.getOrNull() }

        val productsResp = productsDeferred.await()
        val products = productsResp.body()?.map { it.toDomain() } ?: emptyList()

        val adsFromApi = adsDeferred.await()?.body()?.map { it.toDomain() }.orEmpty()
        val catsFromApi = catsDeferred.await()?.body()?.map { it.toDomain() }.orEmpty()

        val ads = if (adsFromApi.isNotEmpty()) adsFromApi else fallbackAds()

        val categories = when {
            catsFromApi.isNotEmpty() -> catsFromApi

            // если нет /categories — берём фиксированный список (как ты и хотел)
            fallbackCategories.isNotEmpty() -> fallbackCategories

            // (на всякий) если вообще не хочешь фикс, можно строить из типов продуктов
            else -> products.map { it.categoryId }
                .filter { it.isNotBlank() }
                .distinct()
                .map { type -> Category(id = type, title = type.replaceFirstChar { c -> c.uppercase() }) }
        }

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
private val fallbackCategories = listOf(
    Category("food", "Еда"),
    Category("clothes", "Одежда"),
    Category("art", "Искусство"),
    Category("craft", "Ремесло"),
    Category("gifts", "Подарки"),
    Category("holiday", "Праздники"),
    Category("home", "Для дома")
)

private fun fallbackAds() = listOf(
    AdBanner(id = "local-1", title = "Супер скидки!", imageRes = com.example.ozmade.R.drawable.banner1),
)