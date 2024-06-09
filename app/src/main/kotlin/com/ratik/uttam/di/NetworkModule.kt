package com.ratik.uttam.di

import com.ratik.uttam.BuildConfig
import com.ratik.uttam.data.ApiConstants.BASE_URL
import com.squareup.moshi.Moshi
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
import retrofit2.converter.moshi.MoshiConverterFactory

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
    moshiConverterFactory: MoshiConverterFactory,
  ): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(gsonConverterFactory) // todo: remove post migration to moshi
      .addConverterFactory(moshiConverterFactory)
      .build()
  }

  @Singleton
  @Provides
  fun provideGson(): GsonConverterFactory {
    return GsonConverterFactory.create()
  }

  @Singleton
  @Provides
  fun provideMoshi(): Moshi {
    return Moshi.Builder().build()
  }

  @Singleton
  @Provides
  fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
    return MoshiConverterFactory.create(moshi)
  }
}

private const val NETWORK_TIMEOUT = 30L
