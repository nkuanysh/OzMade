package com.example.ozmade.network.auth

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.AuthSyncResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    suspend fun syncUserWithBackend(): Result<AuthSyncResponse>
}

@Singleton
class RealAuthRepository @Inject constructor(
    private val api: OzMadeApi,
    private val sessionStore: SessionStore // ✅ ДОБАВИЛИ
) : AuthRepository {

    override suspend fun syncUserWithBackend(): Result<AuthSyncResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.syncUser()

                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("Backend sync failed with code: ${response.code()}")
                    )
                }

                val body = response.body()
                    ?: return@withContext Result.failure(Exception("Empty body from syncUser()"))

                sessionStore.setUserId(body.userId)

                Result.success(body)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

