package com.subhash.subhashtask.di

import android.content.Context
import androidx.room.Room
import com.subhash.subhashtask.data.local.dao.HoldingDao
import com.subhash.subhashtask.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "portfolio_db"
        ).build()
    }

    @Provides
    fun provideHoldingDao(db: AppDatabase): HoldingDao = db.holdingDao()
}
