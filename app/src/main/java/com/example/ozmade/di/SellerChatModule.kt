package com.example.ozmade.di

import com.example.ozmade.main.seller.chat.data.RealSellerChatRepository
import com.example.ozmade.main.seller.chat.data.SellerChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SellerChatModule {

    @Binds
    @Singleton
    abstract fun bindSellerChatRepository(
        impl: RealSellerChatRepository
    ): SellerChatRepository
}