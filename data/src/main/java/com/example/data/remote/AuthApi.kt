package com.example.data.remote

import com.example.data.model.AuthResponseDto
import com.example.data.model.GoogleLoginRequestDto
import com.example.data.model.LoginRequestDto
import com.example.data.model.OtpResponseDto
import com.example.data.model.PhoneRequestDto
import com.example.data.model.VerifyOtpRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("/auth/register/buyer")
    suspend fun registerBuyer(@Body request: PhoneRequestDto): OtpResponseDto

    @POST("/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestDto): AuthResponseDto

    @Multipart
    @POST("/auth/register/seller")
    suspend fun registerSeller(
        @Part("phone") phone: RequestBody,
        @Part("full_name") fullName: RequestBody,
        @Part idCard: MultipartBody.Part?
    ): AuthResponseDto

    @POST("/auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequestDto): AuthResponseDto
}
