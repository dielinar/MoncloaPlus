package com.example.moncloaplus.model.service.module

import com.example.moncloaplus.model.service.AccountService
import com.example.moncloaplus.model.service.MealsService
import com.example.moncloaplus.model.service.ReservationService
import com.example.moncloaplus.model.service.StorageService
import com.example.moncloaplus.model.service.impl.AccountServiceImpl
import com.example.moncloaplus.model.service.impl.MealsServiceImpl
import com.example.moncloaplus.model.service.impl.ReservationServiceImpl
import com.example.moncloaplus.model.service.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService
    @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService
    @Binds abstract fun provideMealsService(impl: MealsServiceImpl): MealsService
    @Binds abstract fun provideReservationService(impl: ReservationServiceImpl): ReservationService
}