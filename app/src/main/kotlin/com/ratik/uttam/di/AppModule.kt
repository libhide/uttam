package com.ratik.uttam.di

import android.app.Application
import android.content.Context
import com.ratik.uttam.core.StringProviderImpl
import com.ratik.uttam.core.ErrorHandler
import com.ratik.uttam.core.ErrorHandlerImpl
import com.ratik.uttam.core.StringProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideApplicationContext(application: Application): Context =
        application.applicationContext

    @Provides
    fun providesResourceProvider(resourceProvider: StringProviderImpl): StringProvider =
        resourceProvider

    @Provides
    fun provideErrorHandler(errorHandler: ErrorHandlerImpl): ErrorHandler = errorHandler
}
