package com.example.ozmade.network.upload

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadService {
    private val client = OkHttpClient()

    suspend fun putFile(
        uploadUrl: String,
        file: File,
        mimeType: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

            val request = Request.Builder()
                .url(uploadUrl)
                .put(requestBody)
                .addHeader("Content-Type", mimeType)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    error("Upload failed: ${response.code} ${response.message}")
                }
            }
        }
    }
}