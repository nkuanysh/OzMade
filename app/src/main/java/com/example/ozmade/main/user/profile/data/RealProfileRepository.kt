package com.example.ozmade.main.user.profile.data

import com.example.ozmade.api.Api
import com.example.ozmade.models.Profile

class RealProfileRepository(private val api: Api) {

    // other functions...

    fun getMyProfile(): Profile {
        api.syncUser()  // Call syncUser first
        return api.getProfile()  // Then call getProfile
    }

    // other functions...
}