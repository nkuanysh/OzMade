package com.example.ozmade.utils

import android.net.Uri

object ImageUtils {
    /**
     * Formats a raw image URL or filename from the backend into a valid, direct 
     * storage.googleapis.com link that can be loaded by Coil.
     */
    fun formatImageUrl(url: String?): String {
        if (url.isNullOrBlank()) return ""
        
        var formatted = url.trim()

        // 1. Skip broken root URLs (e.g. https://storage.googleapis.com?X-Goog-Algorithm...)
        if (formatted.startsWith("https://storage.googleapis.com?") || 
            formatted == "https://storage.googleapis.com" ||
            formatted == "https://storage.googleapis.com/") {
            return ""
        }

        // 2. Handle double-prefixing
        if (formatted.startsWith("https://storage.googleapis.com/https%3A//") || 
            formatted.startsWith("https://storage.googleapis.com/http")) {
            val decoded = Uri.decode(formatted.substringAfter("https://storage.googleapis.com/"))
            return formatImageUrl(decoded)
        }

        // 3. Convert cloud console links to direct API links
        if (formatted.contains("storage.cloud.google.com")) {
            formatted = formatted.replace("storage.cloud.google.com", "storage.googleapis.com")
        }

        // 4. Fix malformed storage.googleapis.com paths
        if (formatted.contains("storage.googleapis.com")) {
            val uri = Uri.parse(formatted)
            val path = uri.path ?: ""
            val filename = path.substringAfterLast("/")
            
            if (filename.isBlank() || filename == "oz-made" || filename == "products") {
                return ""
            }

            // If path is missing bucket prefix, the signature is invalid. Return clean public link.
            if (!path.contains("/oz-made/")) {
                formatted = "https://storage.googleapis.com/oz-made/products/$filename"
            } else if (formatted.contains("authuser=")) {
                // Strip authuser which causes 403 in many mobile clients
                val newUri = uri.buildUpon().clearQuery().apply {
                    uri.queryParameterNames.forEach { name ->
                        if (name != "authuser") {
                            appendQueryParameter(name, uri.getQueryParameter(name))
                        }
                    }
                }.build()
                formatted = newUri.toString()
            }
        } else if (!formatted.startsWith("http")) {
            // 5. Prepend base URL for raw filenames
            val cleanName = if (formatted.startsWith("/")) formatted.substring(1) else formatted
            formatted = "https://storage.googleapis.com/oz-made/products/$cleanName"
        }
        
        return formatted
    }
}
