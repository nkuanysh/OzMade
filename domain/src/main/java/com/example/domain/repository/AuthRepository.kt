package com.example.domain.repository

import com.example.domain.entities.User
import java.io.File
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(phone: String, password: String): Flow<Result<User>>

    // Buyer Flow
    suspend fun registerBuyer(phone: String): Flow<Result<String>> // Returns OTP session ID or message
    suspend fun verifyOtp(phone: String, otp: String): Flow<Result<User>>

    // Seller Flow
    suspend fun registerSeller(
        phone: String,
        fullName: String,
        idCard: File? // Nullable as per TODO
    ): Flow<Result<User>>

    suspend fun googleLogin(idToken: String): Flow<Result<User>>
}