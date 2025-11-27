package com.agroberriesmx.combustiblesagroberries.data.local

import android.content.Context
import com.agroberriesmx.combustiblesagroberries.data.RecordsRepositoryImpl
import com.agroberriesmx.combustiblesagroberries.domain.RecordsRepository
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
    fun provideDatabaseHelper(@ApplicationContext context: Context): DatabaseHelper{
        return DatabaseHelper(context)
    }

    @Provides
    @Singleton
    fun provideCombustiblesLocalDBService(databaseHelper: DatabaseHelper): CombustiblesLocalDBService{
        return CombustiblesLocalDBServiceImpl(databaseHelper)
    }

    @Provides
    @Singleton
    fun provideRecordsRepository(localDBService: CombustiblesLocalDBService): RecordsRepository {
        return RecordsRepositoryImpl(localDBService)
    }
}