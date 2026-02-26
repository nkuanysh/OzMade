package com.example.ozmade.network.auth

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStore @Inject constructor() {
    private var userId: Int? = null
    fun setUserId(id: Int) { userId = id }
    fun myUserId(): Int? = userId
}