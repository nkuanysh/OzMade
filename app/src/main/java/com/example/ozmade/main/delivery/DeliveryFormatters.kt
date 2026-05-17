package com.example.ozmade.main.delivery

import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Locale
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

fun formatDeliveryPrice(price: Int, currency: String = "₸"): String = "$price $currency"

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

    knownCities.firstOrNull { city ->
        text.contains(city, ignoreCase = true)
    }?.let { return it }

    return text.split(',', ';')
        .map { it.trim() }
        .firstOrNull { it.isNotBlank() }
        ?.removePrefix("г.")
        ?.removePrefix("город")
        ?.trim()
        ?.takeIf { it.isNotBlank() }
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
