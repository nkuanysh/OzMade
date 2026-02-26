package com.example.ozmade.main.user.chat.di

import com.example.ozmade.main.user.chat.data.ChatRepository
import com.example.ozmade.main.user.chat.data.RealChatRepository // ✅ ВОТ ТАК
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: RealChatRepository
    ): ChatRepository
}