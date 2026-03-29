package com.example.ozmade.utils

import android.net.Uri

object ImageUtils {
    /**
     * Formats a raw image URL or filename from the backend into a valid, direct 
     * storage.googleapis.com link that can be loaded by Coil.
     */
    fun formatImageUrl(url: String?): String {
        if (url.isNullOrBlank()) return ""
        
        var formatted = url

        // 1. If it's a broken URL pointing only to the bucket/domain
        if (formatted.endsWith("storage.googleapis.com/") || 
            formatted.endsWith("storage.googleapis.com/oz-made") ||
            formatted.contains("storage.googleapis.com/oz-made?")) {
            // Check if it's just the bucket without a filename
            val uri = Uri.parse(formatted)
            if (uri.path == "/oz-made" || uri.path == "/" || uri.path.isNullOrBlank()) {
                return ""
            }
        }

        // 2. Handle double-prefixing: "https://storage.googleapis.com/https%3A//..."
        if (formatted.startsWith("https://storage.googleapis.com/https%3A//") || 
            formatted.startsWith("https://storage.googleapis.com/http")) {
            val decoded = Uri.decode(formatted.substringAfter("https://storage.googleapis.com/"))
            return formatImageUrl(decoded)
        }

        // 3. Convert cloud console links (storage.cloud.google.com) to direct API links
        if (formatted.contains("storage.cloud.google.com")) {
            formatted = formatted.replace("storage.cloud.google.com", "storage.googleapis.com")
            // If it has params like ?authuser, they won't work with the direct API host, so strip them
            if (formatted.contains("?")) {
                formatted = formatted.substringBefore("?")
            }
            return formatted
        }

        // 4. If it's already a valid signed URL from storage.googleapis.com, KEEP IT
        // We must NOT strip the signature parameters or the request will fail with 403.
        if (formatted.contains("storage.googleapis.com") && formatted.contains("X-Goog-Algorithm")) {
            return formatted
        }

        // 5. If it's just a filename, prepend the storage base URL.
        if (!formatted.startsWith("http")) {
            val cleanName = if (formatted.startsWith("/")) formatted.substring(1) else formatted
            // Using 'oz-made' as the bucket based on your latest logs
            return "https://storage.googleapis.com/oz-made/$cleanName"
        }
        
        return formatted
    }
}
