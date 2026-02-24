package com.example.ozmade.di

import com.example.ozmade.main.userHome.details.ProductRepository
import com.example.ozmade.main.userHome.details.RealProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: RealProductRepository
    ): ProductRepository

}
