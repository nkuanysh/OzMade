package com.example.ozmade.di

import com.example.ozmade.main.seller.quality.data.RealSellerQualityRepository
import com.example.ozmade.main.seller.quality.data.SellerQualityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SellerQualityModule {
    @Binds
    @Singleton
    abstract fun bindSellerQualityRepository(
        impl: RealSellerQualityRepository
    ): SellerQualityRepository
}