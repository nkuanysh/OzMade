package com.example.ozmade.main.userHome.details

import kotlin.math.*

private const val EARTH_RADIUS_KM = 6371.0

fun isInsideDeliveryZone(
    buyerLat: Double?,
    buyerLng: Double?,
    sellerLat: Double?,
    sellerLng: Double?,
    radiusKm: Double?
): Boolean? {
    if (buyerLat == null || buyerLng == null || sellerLat == null || sellerLng == null || radiusKm == null) {
        return null
    }

    val distance = haversineKm(
        lat1 = buyerLat,
        lon1 = buyerLng,
        lat2 = sellerLat,
        lon2 = sellerLng
    )

    return distance <= radiusKm
}

private fun haversineKm(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_KM * c
}