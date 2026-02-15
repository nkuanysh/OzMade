package com.example.data.di

//import com.example.data.impl.AuthRepositoryImpl
import com.example.data.impl.FirebaseAuthRepository
import com.example.data.impl.FirebaseAuthRepositoryImpl
import com.example.data.remote.AuthApi
import com.example.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi {
        return Retrofit.Builder()
            .baseUrl("https://api.ozmade.com/") // Replace with actual URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<AuthApi>()
    }

//    @Provides
//    @Singleton
//    fun provideAuthRepository(api: AuthApi): AuthRepository {
//        return AuthRepositoryImpl(api)
//    }
@Provides
@Singleton
fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthRepository(auth: FirebaseAuth): FirebaseAuthRepository {
        return FirebaseAuthRepositoryImpl(auth)
    }

}
