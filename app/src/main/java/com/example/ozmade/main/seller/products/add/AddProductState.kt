package com.example.ozmade.main.seller.products.add

import android.net.Uri
import com.example.ozmade.R

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
    val titleRes: Int,
    val backendValue: String
) {
    FOOD("Еда", R.string.category_food, "food"),
    CLOTHES("Одежда", R.string.category_clothes, "clothes"),
    ART("Искусство", R.string.category_art, "art"),
    CRAFTS("Ремесло", R.string.category_crafts, "crafts"),
    GIFTS("Подарки", R.string.category_gifts, "gifts"),
    HOME("Для Дома", R.string.category_home, "home")
}