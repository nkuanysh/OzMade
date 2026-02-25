package com.example.ozmade.main.orders.data

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

    val sellerName: String?,

    val deliveryType: String, // PICKUP/MY_DELIVERY/INTERCITY
    val pickupAddress: String?,
    val pickupTime: String?,
    val zoneCenterAddress: String?,
    val zoneRadiusKm: Int?,
    val shippingAddressText: String?,

    val confirmCode: String?
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

fun statusTitle(s: String): String = when (s) {
    OrderStatus.PENDING_SELLER -> "Ожидает подтверждения"
    OrderStatus.CONFIRMED -> "Подтверждён"
    OrderStatus.READY_OR_SHIPPED -> "Готов / Отправлен"
    OrderStatus.COMPLETED -> "Завершён"
    OrderStatus.CANCELLED_BY_BUYER -> "Отменён покупателем"
    OrderStatus.CANCELLED_BY_SELLER -> "Отменён продавцом"
    OrderStatus.EXPIRED -> "Истёк"
    else -> s
}

fun deliveryTitle(type: String): String = when (type) {
    DeliveryType.PICKUP -> "Самовывоз"
    DeliveryType.MY_DELIVERY -> "Моя доставка"
    DeliveryType.INTERCITY -> "Межгород"
    else -> type
}