package com.example.ozmade.main.delivery

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.roundToInt

private val ruMonths = listOf(
    "января",
    "февраля",
    "марта",
    "апреля",
    "мая",
    "июня",
    "июля",
    "августа",
    "сентября",
    "октября",
    "ноября",
    "декабря"
)

fun formatDeliveryDateRange(fromIso: String, toIso: String): String {
    val from = parseIsoDate(fromIso) ?: return "$fromIso - $toIso"
    val to = parseIsoDate(toIso) ?: return "$fromIso - $toIso"

    return if (from.month == to.month && from.year == to.year) {
        "${from.dayOfMonth}–${to.dayOfMonth} ${ruMonths[to.monthValue - 1]}"
    } else {
        "${from.dayOfMonth} ${ruMonths[from.monthValue - 1]} – ${to.dayOfMonth} ${ruMonths[to.monthValue - 1]}"
    }
}

fun formatDeliveryPrice(price: Double, currency: String = "₸"): String {
    val amount = if (price % 1.0 == 0.0) {
        price.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", price)
    }
    return "$amount ${currency.ifBlank { "₸" }}"
}

fun extractCity(address: String?): String? {
    val text = address?.trim().orEmpty()
    if (text.isBlank()) return null

    val knownCities = listOf(
        "Алматы",
        "Астана",
        "Шымкент",
        "Караганда",
        "Актобе",
        "Тараз",
        "Павлодар",
        "Усть-Каменогорск",
        "Семей",
        "Атырау",
        "Костанай",
        "Кызылорда",
        "Актау",
        "Уральск",
        "Петропавловск",
        "Туркестан",
        "Кокшетау",
        "Талдыкорган"
    )

    canonicalCityAlias(text)?.let { return it }

    knownCities.firstOrNull { city ->
        text.contains(city, ignoreCase = true)
    }?.let { return it }

    return text.split(',', ';')
        .map { it.trim() }
        .firstOrNull { it.isNotBlank() }
        ?.removePrefix("г.")
        ?.removePrefix("город")
        ?.trim()
        ?.let { canonicalCityAlias(it) ?: it }
        ?.takeIf { it.isNotBlank() && !it.looksLikeStreetName() && !it.isCountryOnly() && !it.looksLikeAdministrativeArea() }
}

suspend fun resolveCityFromCoordinates(
    context: Context,
    latitude: Double?,
    longitude: Double?
): String? {
    if (latitude == null || longitude == null) return null

    return withContext(Dispatchers.IO) {
        nearestKnownCity(latitude, longitude)?.let { return@withContext it }

        try {
            val geocoder = Geocoder(context, Locale("ru", "KZ"))
            val addresses: List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 3) { result ->
                        continuation.resume(result.orEmpty())
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 3).orEmpty()
            }

            addresses.asSequence()
                .flatMap { address ->
                    sequenceOf(
                        address.locality,
                        address.subAdminArea,
                        address.adminArea
                    )
                }
                .mapNotNull { it?.trim()?.takeIf { value -> value.isNotBlank() } }
                .map { it.removeSuffix(" қаласы").removeSuffix(" город").trim() }
                .map { canonicalCityAlias(it) ?: it }
                .firstOrNull { !it.looksLikeStreetName() && !it.isCountryOnly() && !it.looksLikeAdministrativeArea() }
        } catch (_: Exception) {
            null
        }
    }
}

private fun String.looksLikeStreetName(): Boolean {
    val normalized = lowercase(Locale.ROOT).trim()
    val streetPrefixes = listOf(
        "ул.",
        "улица",
        "просп.",
        "проспект",
        "пр-т",
        "мкр",
        "микрорайон",
        "пер.",
        "переулок",
        "шоссе",
        "street",
        "avenue",
        "ave"
    )
    return streetPrefixes.any { normalized.startsWith(it) }
}

private fun String.isCountryOnly(): Boolean {
    val normalized = lowercase(Locale.ROOT).trim()
    return normalized == "казахстан" || normalized == "қазақстан" || normalized == "kazakhstan"
}

private fun String.looksLikeAdministrativeArea(): Boolean {
    val normalized = lowercase(Locale.ROOT).trim()
    return normalized.contains("область") ||
        normalized.contains("облысы") ||
        normalized.contains("region") ||
        normalized.contains("район") ||
        normalized.contains("ауданы") ||
        normalized.contains("district")
}

private fun canonicalCityAlias(value: String): String? {
    val normalized = value
        .lowercase(Locale.ROOT)
        .replace("ё", "е")
        .replace("ұ", "у")
        .replace("ү", "у")
        .replace("ә", "а")
        .trim()

    return when {
        normalized == "алма-ата" -> "Алматы"
        normalized == "нур-султан" || normalized == "нұр-сұлтан" -> "Астана"
        normalized == "жамбыл" || normalized == "джамбул" -> "Тараз"
        normalized.contains("байзак") -> "Тараз"
        else -> null
    }
}

private fun nearestKnownCity(latitude: Double, longitude: Double): String? {
    val cities = listOf(
        KnownCity("Алматы", 43.238949, 76.889709),
        KnownCity("Астана", 51.160522, 71.470360),
        KnownCity("Шымкент", 42.341684, 69.590101),
        KnownCity("Тараз", 42.899994, 71.366667),
        KnownCity("Караганда", 49.806879, 73.085705),
        KnownCity("Актобе", 50.283933, 57.166978),
        KnownCity("Павлодар", 52.287303, 76.967402),
        KnownCity("Усть-Каменогорск", 49.948913, 82.627461),
        KnownCity("Семей", 50.411111, 80.227500),
        KnownCity("Атырау", 47.094496, 51.923837),
        KnownCity("Костанай", 53.214481, 63.624630),
        KnownCity("Кызылорда", 44.848831, 65.482268),
        KnownCity("Актау", 43.653226, 51.197456),
        KnownCity("Уральск", 51.227821, 51.386543),
        KnownCity("Петропавловск", 54.872790, 69.143000),
        KnownCity("Туркестан", 43.297330, 68.251750),
        KnownCity("Кокшетау", 53.283333, 69.383333),
        KnownCity("Талдыкорган", 45.017711, 78.380441)
    )

    return cities
        .minByOrNull { distanceKm(latitude, longitude, it.latitude, it.longitude) }
        ?.takeIf { distanceKm(latitude, longitude, it.latitude, it.longitude) <= 80.0 }
        ?.name
}

private data class KnownCity(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
        kotlin.math.cos(Math.toRadians(lat1)) *
        kotlin.math.cos(Math.toRadians(lat2)) *
        kotlin.math.sin(dLon / 2) *
        kotlin.math.sin(dLon / 2)
    return earthRadiusKm * 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
}

fun parseWeightGrams(raw: String?): Int {
    val text = raw?.lowercase(Locale.ROOT)?.replace(',', '.')?.trim().orEmpty()
    val number = Regex("""\d+(\.\d+)?""").find(text)?.value?.toDoubleOrNull()
        ?: return DEFAULT_WEIGHT_GRAMS

    return when {
        text.contains("кг") || text.contains("kg") -> (number * 1000).roundToInt()
        text.contains("г") || text.contains("g") -> number.roundToInt()
        number <= 30.0 -> (number * 1000).roundToInt()
        else -> number.roundToInt()
    }.coerceAtLeast(1)
}

fun parseDimensionCm(raw: String?, defaultValue: Int): Int {
    val text = raw?.lowercase(Locale.ROOT)?.replace(',', '.')?.trim().orEmpty()
    return Regex("""\d+(\.\d+)?""")
        .find(text)
        ?.value
        ?.toDoubleOrNull()
        ?.roundToInt()
        ?.coerceAtLeast(1)
        ?: defaultValue
}

private fun parseIsoDate(value: String): LocalDate? {
    return try {
        LocalDate.parse(value.take(10))
    } catch (_: DateTimeParseException) {
        null
    }
}
