package com.example.ozmade.di

import com.example.ozmade.auth.FirebaseAuthRepository
import com.example.ozmade.auth.FirebaseAuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindFirebaseAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): FirebaseAuthRepository
}