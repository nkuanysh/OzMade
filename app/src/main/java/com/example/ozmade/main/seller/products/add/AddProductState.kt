package com.example.ozmade.main.seller.products.add

import android.net.Uri

private const val MAX_PHOTOS = 10

data class AddProductState(
    val photos: List<Uri> = emptyList(),
    val title: String = "",
    val priceText: String = "",
    val selectedCategories: Set<SellerCategory> = emptySet(),

    // если потом понадобится отдельный type/address — можно будет управлять ими из UI
    val type: String = "",
    val address: String = "",

    val weightText: String = "",
    val heightText: String = "",
    val widthText: String = "",
    val depthText: String = "",
    val composition: String = "",

    val description: String = "",
    val youtubeUrl: String = "",

    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
) {
    val canAddMorePhotos: Boolean = photos.size < MAX_PHOTOS

//    val priceValue: Double?
//        get() = priceText.replace(',', '.').toDoubleOrNull()
    val priceValue: Double? = priceText.replace(',', '.').toDoubleOrNull()


    val isValid: Boolean
        get() = photos.isNotEmpty() &&
                title.trim().isNotBlank() &&
                (priceValue != null && priceValue > 0.0) &&
                selectedCategories.isNotEmpty() &&
                description.trim().isNotBlank()
}

enum class SellerCategory(
    val title: String,
    val backendValue: String
) {
    FOOD("Еда", "food"),
    CLOTHES("Одежда", "clothes"),
    ART("Искусство", "art"),
    CRAFTS("Ремесло", "crafts"),
    GIFTS("Подарки", "gifts"),
    HOME("Для Дома", "home")
}