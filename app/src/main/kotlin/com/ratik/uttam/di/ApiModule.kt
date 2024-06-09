package com.ratik.uttam.di

import com.ratik.uttam.data.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

  @Provides fun provideUnsplashApi(retrofit: Retrofit): UnsplashApi = retrofit.create()
}
