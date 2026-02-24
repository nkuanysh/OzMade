package com.example.ozmade.di

import com.example.ozmade.main.home.HomeRepository
import com.example.ozmade.main.home.RealHomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        impl: RealHomeRepository
    ): HomeRepository

}
