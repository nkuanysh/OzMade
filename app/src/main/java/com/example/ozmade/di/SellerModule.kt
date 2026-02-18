package com.example.ozmade.di
import com.example.ozmade.main.seller.FakeSellerRepository
import com.example.ozmade.main.seller.SellerRepository
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
    abstract fun bindSellerRepository(
        impl: FakeSellerRepository
    ): SellerRepository
}
