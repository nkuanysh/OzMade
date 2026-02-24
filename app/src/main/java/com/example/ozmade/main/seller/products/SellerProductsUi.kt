package com.example.ozmade.main.seller.products

enum class SellerProductStatus {
    ON_SALE,
    OFF_SALE,
    PENDING_MODERATION
}

data class SellerProductUi(
    val id: Int,
    val title: String,
    val price: Int,
    val imageUrl: String?,
    val status: SellerProductStatus
)

enum class SellerProductsFilter(val title: String) {
    ALL("Все"),
    ON_SALE("В продаже"),
    OFF_SALE("Сняты с продажи"),
    PENDING("Ожидает модерации")
}

fun SellerProductsFilter.matches(status: SellerProductStatus): Boolean = when (this) {
    SellerProductsFilter.ALL -> true
    SellerProductsFilter.ON_SALE -> status == SellerProductStatus.ON_SALE
    SellerProductsFilter.OFF_SALE -> status == SellerProductStatus.OFF_SALE
    SellerProductsFilter.PENDING -> status == SellerProductStatus.PENDING_MODERATION
}

fun SellerProductStatus.title(): String = when (this) {
    SellerProductStatus.ON_SALE -> "В продаже"
    SellerProductStatus.OFF_SALE -> "Сняты с продажи"
    SellerProductStatus.PENDING_MODERATION -> "Ожидает проверку"
}