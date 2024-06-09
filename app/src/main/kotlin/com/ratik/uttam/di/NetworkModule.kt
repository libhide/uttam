package com.ratik.uttam.di

import com.ratik.uttam.BuildConfig
import com.ratik.uttam.data.ApiConstants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideHttpClient(): OkHttpClient {
    val builder =
      OkHttpClient.Builder()
        .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
      val loggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
      builder.addInterceptor(loggingInterceptor)
    }

    return builder.build()
  }

  @Singleton
  @Provides
  fun providesRetrofit(
    okHttpClient: OkHttpClient,
    gsonConverterFactory: GsonConverterFactory,
  ): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(gsonConverterFactory)
      .build()
  }

  @Singleton
  @Provides
  fun provideGson(): GsonConverterFactory {
    return GsonConverterFactory.create()
  }
}

private const val NETWORK_TIMEOUT = 30L
