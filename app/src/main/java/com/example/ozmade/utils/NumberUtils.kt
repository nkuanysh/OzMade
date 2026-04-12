package com.example.ozmade.utils

import java.util.Locale

fun formatRating(rating: Double): String {
    return if (rating == 0.0) "0.0" else String.format(Locale.US, "%.1f", rating)
}
