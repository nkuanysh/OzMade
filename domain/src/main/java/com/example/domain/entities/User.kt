package com.example.domain.entities

data class User(
    val id: Int,
    val username: String,
    val fullName: String,
    val phoneNumber: String,
    val token: String,
    val role: UserRole
)

enum class UserRole { BUYER, SELLER }