package com.example.ozmade.di

import com.example.ozmade.main.seller.data.SellerRepository
import com.example.ozmade.main.seller.data.SellerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SellerModule {

    @Binds
    @Singleton
    abstract fun bindSellerRepository(impl: SellerRepositoryImpl): SellerRepository
}