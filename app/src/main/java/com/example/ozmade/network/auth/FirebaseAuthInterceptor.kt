package com.example.ozmade.network.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FirebaseAuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val user = firebaseAuth.currentUser
        val original = chain.request()

        // Если не залогинен — отправляем как есть
        if (user == null) return chain.proceed(original)

        // Берём ID token синхронно (через runBlocking внутри interceptor — норм для MVP)
        val token = runBlocking {
            runCatching { user.getIdToken(false).await().token }.getOrNull()
        }

        val req = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else original

        return chain.proceed(req)
    }
}
