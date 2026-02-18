package com.example.ozmade.di

import com.example.ozmade.network.api.ProfileApi
import com.example.ozmade.network.auth.FirebaseAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.example.ozmade.network.api.HomeApi
import com.example.ozmade.network.api.ProductApi
import com.example.ozmade.network.api.ReviewsApi
import com.example.ozmade.network.api.SellerApi
import com.example.ozmade.network.api.SellerReviewsApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // TODO: когда бэкенд будет готов — поставишь сюда реальный URL
    private const val BASE_URL = "https://example.com/"


    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: FirebaseAuthInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // важно: должен заканчиваться на /
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi =
        retrofit.create(ProfileApi::class.java)

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi =
        retrofit.create(HomeApi::class.java)

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi =
        retrofit.create(ProductApi::class.java)

    @Provides
    @Singleton
    fun provideReviewsApi(retrofit: Retrofit): ReviewsApi =
        retrofit.create(ReviewsApi::class.java)

    @Provides
    @Singleton
    fun provideSellerApi(retrofit: Retrofit): SellerApi =
        retrofit.create(SellerApi::class.java)

    @Provides
    @Singleton
    fun provideSellerReviewsApi(retrofit: Retrofit): SellerReviewsApi =
        retrofit.create(SellerReviewsApi::class.java)




}

