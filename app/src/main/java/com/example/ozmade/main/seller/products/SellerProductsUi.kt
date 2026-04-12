package com.example.ozmade.main.seller.products

enum class SellerProductStatus {
    ON_SALE,
    OFF_SALE
}

data class SellerProductUi(
    val id: Int,
    val title: String,
    val price: Int,
    val imageUrl: String?,
    val status: SellerProductStatus,
    val viewCount: Int = 0
)

enum class SellerProductsFilter(val title: String) {
    ALL("Все"),
    ON_SALE("В продаже"),
    OFF_SALE("Сняты с продажи")
}

fun SellerProductsFilter.matches(status: SellerProductStatus): Boolean = when (this) {
    SellerProductsFilter.ALL -> true
    SellerProductsFilter.ON_SALE -> status == SellerProductStatus.ON_SALE
    SellerProductsFilter.OFF_SALE -> status == SellerProductStatus.OFF_SALE
}

fun SellerProductStatus.title(): String = when (this) {
    SellerProductStatus.ON_SALE -> "В продаже"
    SellerProductStatus.OFF_SALE -> "Сняты с продажи"
}