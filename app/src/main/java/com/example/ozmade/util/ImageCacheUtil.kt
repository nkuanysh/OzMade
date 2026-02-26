package com.example.ozmade.util

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

object ImageCacheUtil {

    fun copyUriToCache(context: Context, uri: Uri): Uri {
        val resolver = context.contentResolver
        val fileName = "img_${System.currentTimeMillis()}.jpg"
        val outFile = File(context.cacheDir, fileName)

        resolver.openInputStream(uri).use { input ->
            FileOutputStream(outFile).use { output ->
                requireNotNull(input) { "Can't open input stream" }
                input.copyTo(output)
            }
        }
        return outFile.toUri() // file://...
    }
}