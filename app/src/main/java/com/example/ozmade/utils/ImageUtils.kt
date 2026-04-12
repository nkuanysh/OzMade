package com.example.ozmade.utils

import android.net.Uri
import android.util.Log

object ImageUtils {
    private const val TAG = "ImageUtils"

    /**
     * Formats a raw image URL or filename from the backend into a valid link.
     *
     * Since public access has been added to the 'oz-made' bucket, we use the
     * direct Google Cloud Storage public URL. This avoids signature issues
     * and Firebase API overhead.
     */
    fun formatImageUrl(url: String?): String {
        if (url.isNullOrBlank()) return ""
        
        val input = url.trim()
        
        // If it's already a full URL, we trust the backend and return it as is.
        // This handles signed URLs and different storage paths.
        if (input.startsWith("http")) {
            return input
        }
        
        val filename = extractFilename(input) ?: ""

        if (filename.isBlank() || filename == "oz-made" || filename == "products") {
            Log.w(TAG, "formatImageUrl: Could not extract valid filename from: $input")
            return if (input.startsWith("http")) input else ""
        }

        // 2. Reconstruct the direct Public GCS URL.
        // Files are confirmed to be stored in the 'products/' subfolder.
        val formattedUrl = "https://storage.googleapis.com/oz-made/products/$filename"

        Log.d(TAG, "formatImageUrl: Using Public GCS URL -> $formattedUrl")
        return formattedUrl
    }

    /**
     * Formats a raw profile photo URL or filename from the backend into a valid link.
     */
    fun formatProfilePhotoUrl(url: String?): String {
        if (url.isNullOrBlank()) return ""
        val input = url.trim()
        
        // If it's already a full URL, use it directly.
        if (input.startsWith("http")) {
            return input
        }

        val filename = extractFilename(input) ?: ""
        if (filename.isBlank()) return input
        
        // Fallback for raw filenames
        return "https://storage.googleapis.com/oz-made/$filename"
    }

    /**
     * Extracts the filename from a URL or returns the input if it's already a filename.
     */
    fun extractFilename(input: String?): String? {
        if (input.isNullOrBlank()) return null
        if (!input.contains("/")) return input

        return try {
            val uri = Uri.parse(input)
            val path = uri.path ?: return input.substringAfterLast("/")
            val name = path.substringAfterLast("/")
            if (name.isBlank() || name == "oz-made" || name == "products") {
                val lastPart = input.substringAfterLast("/")
                if (lastPart.contains("?")) lastPart.substringBefore("?") else lastPart
            } else {
                name
            }
        } catch (e: Exception) {
            input.substringAfterLast("/").substringBefore("?")
        }
    }
}
