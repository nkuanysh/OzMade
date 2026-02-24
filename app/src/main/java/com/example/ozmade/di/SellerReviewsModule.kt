package com.example.ozmade.di

import com.example.ozmade.main.home.seller.reviews.RealSellerReviewsRepository
import com.example.ozmade.main.home.seller.reviews.SellerReviewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SellerReviewsModule {

    @Binds
    @Singleton
    abstract fun bindSellerReviewsRepository(
        impl: RealSellerReviewsRepository
    ): SellerReviewsRepository
}
