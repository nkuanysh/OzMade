package com.example.ozmade.network.upload

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

            val request = Request.Builder()
                .url(uploadUrl)
                .put(requestBody)
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
            Unit // Обязательно возвращаем Unit, чтобы тип был Result<Unit>
        }
    }
}
