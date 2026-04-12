package com.example.ozmade.di

import com.example.ozmade.main.user.profile.data.ProfileRepository
import com.example.ozmade.main.user.profile.data.RealProfileRepository
import com.example.ozmade.main.user.profile.notification.NotificationRepository
import com.example.ozmade.main.user.profile.notification.RealNotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: RealProfileRepository
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: RealNotificationRepository
    ): NotificationRepository
}
