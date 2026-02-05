package com.example.data.model

import com.example.domain.entities.User
import com.example.domain.entities.UserRole

// --- Request DTOs ---
data class LoginRequestDto(val phone: String, val password: String)
data class PhoneRequestDto(val phone: String)
data class VerifyOtpRequestDto(val phone: String, val otp: String)
data class GoogleLoginRequestDto(val idToken: String)

// --- Response DTOs ---
data class OtpResponseDto(val message: String, val sessionId: String?)
data class AuthResponseDto(val token: String, val userId: String, val name: String, val role: String)

// --- Mappers ---
fun AuthResponseDto.toDomain(phone: String): User {
    val userRole = if (role.equals("seller", ignoreCase = true)) UserRole.SELLER else UserRole.BUYER
    return User(
        id = this.userId,
        username = this.name, // Assuming username and full name are the same from the backend
        fullName = this.name,
        phoneNumber = phone, // Phone number is not in the response, so we pass it from the request
        token = this.token,
        role = userRole
    )
}
