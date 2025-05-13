package com.example.moncloaplus.service.module

import com.example.moncloaplus.service.AccountService
import com.example.moncloaplus.service.EventService
import com.example.moncloaplus.service.FixService
import com.example.moncloaplus.service.MealsService
import com.example.moncloaplus.service.ReservationService
import com.example.moncloaplus.service.StorageService
import com.example.moncloaplus.service.impl.AccountServiceImpl
import com.example.moncloaplus.service.impl.EventServiceImpl
import com.example.moncloaplus.service.impl.FixServiceImpl
import com.example.moncloaplus.service.impl.MealsServiceImpl
import com.example.moncloaplus.service.impl.ReservationServiceImpl
import com.example.moncloaplus.service.impl.StorageServiceImpl
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
    @Binds abstract fun provideFixService(impl: FixServiceImpl): FixService
    @Binds abstract fun provideEventService(impl: EventServiceImpl): EventService
}
