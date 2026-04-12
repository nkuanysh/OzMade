package com.example.ozmade.di

import android.content.Context
import com.example.ozmade.main.user.profile.locale.LanguageStore
import com.example.ozmade.ui.theme.ThemeSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideThemeSettings(@ApplicationContext context: Context): ThemeSettings {
        return ThemeSettings(context)
    }

    @Provides
    @Singleton
    fun provideLanguageStore(@ApplicationContext context: Context): LanguageStore {
        return LanguageStore(context)
    }
}
