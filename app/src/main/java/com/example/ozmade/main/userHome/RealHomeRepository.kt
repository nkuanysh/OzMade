package com.example.ozmade.main.userHome

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.AdDto
import com.example.ozmade.network.model.CategoryDto
import com.example.ozmade.network.model.ProductDto
import com.example.ozmade.utils.ImageUtils
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
        val favoritesDeferred = async { runCatching { api.getFavorites() }.getOrNull() }

        val productsResp = productsDeferred.await()
        val favoriteResp = favoritesDeferred.await()

        val favoriteIds = favoriteResp
            ?.body()
            .orEmpty()
            .map { it.id }
            .toSet()

        val products = productsResp.body()?.map { dto ->
            dto.toDomain(liked = favoriteIds.contains(dto.id))
        } ?: emptyList()

        val adsFromApi = adsDeferred.await()?.body()?.map { it.toDomain() }.orEmpty()
        val catsFromApi = catsDeferred.await()?.body()?.map { it.toDomain() }.orEmpty()

        val ads = if (adsFromApi.isNotEmpty()) adsFromApi else fallbackAds()

        val categories = when {
            catsFromApi.isNotEmpty() -> catsFromApi
            fallbackCategories.isNotEmpty() -> fallbackCategories
            else -> products.map { it.categoryId }
                .filter { it.isNotBlank() }
                .distinct()
                .map { type ->
                    Category(
                        id = type,
                        title = type.replaceFirstChar { c -> c.uppercase() }
                    )
                }
        }

        HomeResponse(
            ads = ads,
            categories = categories,
            products = products
        )
    }

    override suspend fun toggleFavorite(productId: Int): Boolean {
        val response = api.toggleFavorite(productId)

        if (!response.isSuccessful) {
            error("Не удалось изменить избранное (${response.code()})")
        }

        return when (response.body()?.status?.lowercase()) {
            "added" -> true
            "removed" -> false
            else -> {
                api.getFavorites().body()?.any { it.id == productId } == true
            }
        }
    }

    override suspend fun getProductsByCategory(type: String): List<Product> {
        val response = api.getProducts(type = type)
        if (!response.isSuccessful) return emptyList()
        
        val favoriteResp = runCatching { api.getFavorites() }.getOrNull()
        val favoriteIds = favoriteResp?.body().orEmpty().map { it.id }.toSet()

        return response.body()?.map { dto ->
            dto.toDomain(liked = favoriteIds.contains(dto.id))
        } ?: emptyList()
    }
}

private fun AdDto.toDomain(): AdBanner =
    AdBanner(
        id = id,
        imageUrl = ImageUtils.formatImageUrl(imageUrl),
        title = title,
        deeplink = deeplink
    )

private fun CategoryDto.toDomain(): Category =
    Category(
        id = id,
        title = title,
        iconUrl = ImageUtils.formatImageUrl(iconUrl)
    )

private fun ProductDto.toDomain(liked: Boolean = false): Product {
    val url = ImageUtils.formatImageUrl(imageUrl).takeIf { it.isNotBlank() }
        ?: ImageUtils.formatImageUrl(images?.firstOrNull())

    return Product(
        id = id,
        title = title ?: name ?: "Unknown",
        price = cost ?: price ?: 0.0,
        city = address?.substringBefore(",") ?: "Unknown",
        address = address ?: "Unknown",
        rating = averageRating ?: 0.0,
        imageUrl = url,
        categoryId = type ?: "",
        liked = liked
    )
}

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
    AdBanner(
        id = "local-1",
        title = "Супер скидки!",
        imageRes = com.example.ozmade.R.drawable.banner1
    ),
)
