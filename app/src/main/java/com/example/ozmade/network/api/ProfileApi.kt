package com.example.ozmade.network.api

import com.example.ozmade.network.dto.UpdateProfileRequest
import com.example.ozmade.network.dto.UserProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface ProfileApi {

    @GET("/user/profile")
    suspend fun getMyProfile(): UserProfileDto

    @PATCH("/user/profile")
    suspend fun updateMyProfile(@Body body: UpdateProfileRequest): UserProfileDto
}
