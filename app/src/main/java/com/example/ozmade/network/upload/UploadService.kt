package com.example.ozmade.network.upload

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

class UploadService {
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor { message ->
            Log.d("UploadService", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        })
        .build()

    suspend fun putFile(
        uploadUrl: String,
        file: File,
        mimeType: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val mediaType = mimeType.toMediaTypeOrNull()
            val requestBody = file.asRequestBody(mediaType)

            // Use toHttpUrl() to parse the signed URL. This prevents OkHttp from 
            // re-encoding parameters that are already part of the signature.
            val url = uploadUrl.toHttpUrl()

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                // The Content-Type must match EXACTLY what was sent to the backend 
                // when requesting the signed URL.
                .header("Content-Type", mimeType)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    Log.e("UploadService", "Upload failed: ${response.code} ${response.message} | Body: $errorBody")
                    error("Upload failed: ${response.code} ${response.message}")
                } else {
                    Log.d("UploadService", "Upload successful")
                }
            }
            Unit
        }
    }
}
