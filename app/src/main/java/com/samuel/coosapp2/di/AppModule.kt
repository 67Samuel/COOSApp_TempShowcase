package com.samuel.coosapp2.di

import android.app.Application
import androidx.room.Room
import com.samuel.coosapp2.business.datasource.cache.AppDatabase
import com.samuel.coosapp2.business.datasource.cache.AppDatabase.Companion.DATABASE_NAME
import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.datastore.AppDataStore
import com.samuel.coosapp2.business.datasource.datastore.AppDataStoreManager
import com.samuel.coosapp2.business.datasource.network.main.VimeoService
import com.samuel.coosapp2.business.datasource.network.main.VimeoServiceImpl
import com.samuel.coosapp2.presentation.main.MainActivity
import com.samuel.coosapp2.presentation.main.video.MainActivityUtil
import com.samuel.coosapp2.presentation.util.VimeoUtil
import com.vimeo.networking2.VimeoApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataStoreManager(
        application: Application,
    ): AppDataStore {
        return AppDataStoreManager(application)
    }

    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideVimeoService(): VimeoService {
        return VimeoServiceImpl()
    }

    @Singleton
    @Provides
    fun provideVimeoApiClient(
        vimeo: VimeoUtil,
        application: Application, // provided by hilt
    ): VimeoApiClient {
        return vimeo.initVimeoAuthenticatorWithAccessToken(application)
    }

    @Singleton
    @Provides
    fun provideVimeoUtil(): VimeoUtil {
        return VimeoUtil()
    }

    @Singleton
    @Provides
    @Named("provideMainActivity")
    fun provideMainActivity(activity: MainActivity): MainActivityUtil {
        return MainActivityUtil(activity)
    }

    @Singleton
    @Provides
    fun provideAppDb(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
//            .addMigrations()
            // destroys and recreates the databases when there is an error when downgrading to old schema versions.
            // Might want to find a way to make sure this doesn't ever happen in production...
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideVideoDao(db: AppDatabase): VideoDao {
        return db.getVideoDao()
    }

}