package com.example.ozmade.di

import com.example.ozmade.main.delivery.DeliveryEstimateRepository
import com.example.ozmade.main.delivery.MockDeliveryEstimateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeliveryModule {

    @Binds
    @Singleton
    abstract fun bindDeliveryEstimateRepository(
        impl: MockDeliveryEstimateRepository
    ): DeliveryEstimateRepository
}
