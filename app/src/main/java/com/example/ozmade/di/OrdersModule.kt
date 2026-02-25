package com.example.ozmade.di

import com.example.ozmade.main.user.orders.data.BuyerOrdersRepository
import com.example.ozmade.main.user.orders.data.RealBuyerOrdersRepository
import com.example.ozmade.main.seller.orders.data.SellerOrdersRepository
import com.example.ozmade.main.seller.orders.data.RealSellerOrdersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrdersModule {

    @Binds @Singleton
    abstract fun bindBuyerOrdersRepo(impl: RealBuyerOrdersRepository): BuyerOrdersRepository

    @Binds @Singleton
    abstract fun bindSellerOrdersRepo(impl: RealSellerOrdersRepository): SellerOrdersRepository

    @Binds @Singleton
    abstract fun bindOrderFlowRepo(impl: com.example.ozmade.main.user.orderflow.data.RealOrderFlowRepository)
            : com.example.ozmade.main.user.orderflow.data.OrderFlowRepository
}