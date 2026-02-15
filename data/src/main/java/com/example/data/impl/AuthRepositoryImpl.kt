//package com.example.data.impl
//
//import com.example.data.model.GoogleLoginRequestDto
//import com.example.data.model.LoginRequestDto
//import com.example.data.model.PhoneRequestDto
//import com.example.data.model.VerifyOtpRequestDto
//import com.example.data.model.toDomain
//import com.example.data.remote.AuthApi
//import com.example.domain.entities.User
//import com.example.domain.repository.AuthRepository
//import java.io.File
//import javax.inject.Inject
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//
//class AuthRepositoryImpl @Inject constructor(private val api: AuthApi) : AuthRepository {
//
//    override suspend fun login(phone: String, password: String): Flow<Result<User>> = flow {
//        try {
//            val response = api.login(LoginRequestDto(phone, password))
//            emit(Result.success(response.toDomain(phone)))
//        } catch (e: Exception) {
//            emit(Result.failure(e))
//        }
//    }
//
//    override suspend fun registerBuyer(phone: String): Flow<Result<String>> = flow {
//        try {
//            val response = api.registerBuyer(PhoneRequestDto(phone))
//            emit(Result.success(response.message))
//        } catch (e: Exception) {
//            emit(Result.failure(e))
//        }
//    }
//
//    override suspend fun verifyOtp(phone: String, otp: String): Flow<Result<User>> = flow {
//        try {
//            val response = api.verifyOtp(VerifyOtpRequestDto(phone, otp))
//            emit(Result.success(response.toDomain(phone)))
//        } catch (e: Exception) {
//            emit(Result.failure(e))
//        }
//    }
//
//    override suspend fun registerSeller(phone: String, fullName: String, idCard: File?): Flow<Result<User>> = flow {
//        try {
//            val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())
//            val namePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
//
//            val idPart = idCard?.let {
//                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
//                MultipartBody.Part.createFormData("electronic_id", it.name, requestFile)
//            }
//
//            val response = api.registerSeller(phonePart, namePart, idPart)
//            emit(Result.success(response.toDomain(phone)))
//        } catch (e: Exception) {
//            emit(Result.failure(e))
//        }
//    }
//
//    override suspend fun googleLogin(idToken: String): Flow<Result<User>> = flow {
//        try {
//            val response = api.googleLogin(GoogleLoginRequestDto(idToken))
//            // The phone number is not available in the Google login flow, so we pass an empty string
//            emit(Result.success(response.toDomain("")))
//        } catch (e: Exception) {
//            emit(Result.failure(e))
//        }
//    }
//}
