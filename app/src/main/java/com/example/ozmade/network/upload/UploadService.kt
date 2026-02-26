package com.example.ozmade.network.upload

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadService(
    private val client: OkHttpClient = OkHttpClient()
) {
    fun putFile(uploadUrl: String, file: File, mimeType: String): Result<Unit> {
        return runCatching {
            val body = file.asRequestBody(mimeType.toMediaType())

            val req = Request.Builder()
                .url(uploadUrl)
                .put(body)
                .build()

            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    error("Upload failed: ${resp.code} ${resp.message}")
                }
            }
        }
    }
}