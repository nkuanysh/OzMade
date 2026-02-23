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
    private val api: OzMadeApi
) : AuthRepository {

    override suspend fun syncUserWithBackend(): Result<AuthSyncResponse> = withContext(Dispatchers.IO) {
        try {
            // The FirebaseAuthInterceptor automatically attaches the token here!
            val response = api.syncUser()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Backend sync failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

