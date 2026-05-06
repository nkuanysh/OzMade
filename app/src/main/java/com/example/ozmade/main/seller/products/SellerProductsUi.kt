package com.example.ozmade.main.seller.products

import com.example.ozmade.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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

enum class SellerProductsFilter(val titleRes: Int) {
    ALL(R.string.all_label),
    ON_SALE(R.string.on_sale),
    OFF_SALE(R.string.off_sale_plural)
}

fun SellerProductsFilter.matches(status: SellerProductStatus): Boolean = when (this) {
    SellerProductsFilter.ALL -> true
    SellerProductsFilter.ON_SALE -> status == SellerProductStatus.ON_SALE
    SellerProductsFilter.OFF_SALE -> status == SellerProductStatus.OFF_SALE
}

@Composable
fun SellerProductStatus.title(): String = when (this) {
    SellerProductStatus.ON_SALE -> stringResource(R.string.on_sale)
    SellerProductStatus.OFF_SALE -> stringResource(R.string.off_sale_plural)
}