package com.example.ozmade.di

import com.example.ozmade.main.user.profile.data.FakeProfileRepository
import com.example.ozmade.main.user.profile.data.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: FakeProfileRepository
    ): ProfileRepository
//    abstract fun bindProfileRepository(impl: RealProfileRepository): ProfileRepository //осыған ауыстыру керек бэк дайын болғанда

}
