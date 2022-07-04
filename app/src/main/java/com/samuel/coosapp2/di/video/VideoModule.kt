package com.samuel.coosapp2.di.video

import com.samuel.coosapp2.business.datasource.datastore.AppDataStore
import com.samuel.coosapp2.business.interactors.video.GetOrderAndFilter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VideoModule {

    @Singleton
    @Provides
    fun provideGetOrderAndFilter(
        appDataStoreManager: AppDataStore
    ): GetOrderAndFilter {
        return GetOrderAndFilter(appDataStoreManager)
    }
}