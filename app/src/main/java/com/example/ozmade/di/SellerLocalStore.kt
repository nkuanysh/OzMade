package com.example.ozmade.di

import android.content.Context
import com.example.ozmade.main.seller.data.SellerLocalStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SellerLocalModule {

    @Provides
    @Singleton
    fun provideSellerLocalStore(@ApplicationContext context: Context): SellerLocalStore {
        return SellerLocalStore(context)
    }
}