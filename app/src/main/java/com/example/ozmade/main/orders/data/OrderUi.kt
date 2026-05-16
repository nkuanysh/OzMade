package com.example.ozmade.main.orders.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.ozmade.R

data class OrderUi(
    val id: Int,
    val status: String,
    val createdAt: String,

    val productId: Int,
    val productTitle: String,
    val productImageUrl: String?,
    val price: Double,
    val quantity: Int,
    val totalCost: Double,

    val sellerId: Int?,
    val sellerName: String?,

    val deliveryType: String, // PICKUP/MY_DELIVERY/INTERCITY
    val pickupAddress: String?,
    val pickupTime: String?,
    val zoneCenterAddress: String?,
    val zoneCenterLat: Double? = null,
    val zoneCenterLng: Double? = null,
    val zoneRadiusKm: Int?,
    val shippingAddressText: String?,
    val shippingLat: Double? = null,
    val shippingLng: Double? = null,
    val shippingComment: String? = null,
    val intercityDelivery: IntercityDeliveryOrderUi? = null,

    val confirmCode: String?,
    val isReviewed: Boolean = false
)

data class IntercityDeliveryOrderUi(
    val provider: String,
    val price: Int,
    val currency: String,
    val minDays: Int,
    val maxDays: Int,
    val estimatedDateFrom: String,
    val estimatedDateTo: String,
    val fromCity: String,
    val fromAddress: String,
    val fromLat: Double? = null,
    val fromLng: Double? = null,
    val toCity: String,
    val toAddress: String,
    val toLat: Double? = null,
    val toLng: Double? = null,
    val receiverName: String,
    val receiverPhone: String,
    val weightGrams: Int,
    val heightCm: Int,
    val widthCm: Int,
    val depthCm: Int,
    val comment: String? = null
)

object OrderStatus {
    const val PENDING_SELLER = "PENDING_SELLER"
    const val CONFIRMED = "CONFIRMED"
    const val READY_OR_SHIPPED = "READY_OR_SHIPPED"
    const val COMPLETED = "COMPLETED"
    const val CANCELLED_BY_BUYER = "CANCELLED_BY_BUYER"
    const val CANCELLED_BY_SELLER = "CANCELLED_BY_SELLER"
    const val EXPIRED = "EXPIRED"
}

object DeliveryType {
    const val PICKUP = "PICKUP"
    const val MY_DELIVERY = "MY_DELIVERY"
    const val INTERCITY = "INTERCITY"
}

@Composable
fun statusTitle(s: String): String = when (s) {
    OrderStatus.PENDING_SELLER -> stringResource(R.string.order_status_pending_seller)
    OrderStatus.CONFIRMED -> stringResource(R.string.order_status_confirmed)
    OrderStatus.READY_OR_SHIPPED -> stringResource(R.string.order_status_ready_or_shipped)
    OrderStatus.COMPLETED -> stringResource(R.string.order_status_completed)
    OrderStatus.CANCELLED_BY_BUYER -> stringResource(R.string.order_status_cancelled_by_buyer)
    OrderStatus.CANCELLED_BY_SELLER -> stringResource(R.string.order_status_cancelled_by_seller)
    OrderStatus.EXPIRED -> stringResource(R.string.order_status_expired)
    else -> s
}

@Composable
fun deliveryTitle(type: String): String = when (type) {
    DeliveryType.PICKUP -> stringResource(R.string.delivery_type_pickup)
    DeliveryType.MY_DELIVERY -> stringResource(R.string.delivery_type_my_delivery)
    DeliveryType.INTERCITY -> stringResource(R.string.delivery_type_intercity)
    else -> type
}
