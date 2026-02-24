package com.example.ozmade.di
import com.example.ozmade.main.home.seller.RealSellerRepository
import com.example.ozmade.main.home.seller.SellerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SellerModuleForUser {

    @Binds
    @Singleton
    abstract fun bindSellerRepository(
        impl: RealSellerRepository
    ): SellerRepository
}
