package com.example.ozmade.di

import com.example.ozmade.main.home.reviews.RealReviewsRepository
import com.example.ozmade.main.home.reviews.ReviewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReviewsModule {

    @Binds
    @Singleton
    abstract fun bindReviewsRepository(
        impl: RealReviewsRepository
    ): ReviewsRepository

}
