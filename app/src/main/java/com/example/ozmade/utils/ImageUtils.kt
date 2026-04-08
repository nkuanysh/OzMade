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
        
        // 1. Extract the CLEAN filename (strip query params and path prefixes)
        val filename = try {
            val uri = Uri.parse(input)
            val path = uri.path ?: ""
            // Extract the last segment (the filename)
            val name = path.substringAfterLast("/")
            if (name.isBlank() || name == "oz-made" || name == "products") {
                // If the path was empty or just the bucket name, check if the input itself is the filename
                if (!input.contains("/") && !input.contains("?")) input else ""
            } else {
                name
            }
        } catch (e: Exception) {
            input.substringAfterLast("/").substringBefore("?")
        }

        if (filename.isBlank() || filename == "oz-made" || filename == "products") {
            Log.w(TAG, "formatImageUrl: Could not extract valid filename from: $input")
            return ""
        }

        // 2. Reconstruct the direct Public GCS URL.
        // Files are confirmed to be stored in the 'products/' subfolder.
        val formattedUrl = "https://storage.googleapis.com/oz-made/products/$filename"
        
        Log.d(TAG, "formatImageUrl: Using Public GCS URL -> $formattedUrl")
        return formattedUrl
    }
}
