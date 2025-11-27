package com.agroberriesmx.combustiblesagroberries.data.network

import com.agroberriesmx.combustiblesagroberries.BuildConfig.BASE_URL
import com.agroberriesmx.combustiblesagroberries.data.RepositoryImpl
import com.agroberriesmx.combustiblesagroberries.data.core.interceptors.AuthInterceptor
import com.agroberriesmx.combustiblesagroberries.domain.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient{
        return OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideCombustiblesApiService(retrofit: Retrofit):CombustiblesApiService{
        return retrofit.create(CombustiblesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCombustiblesRepository(combustiblesApiService: CombustiblesApiService): Repository {
        return RepositoryImpl(combustiblesApiService)
    }

}