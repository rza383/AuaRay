package kz.rza383.auaray.di

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kz.rza383.Constants
import kz.rza383.auaray.data.repository.MyRepositoryImpl
import kz.rza383.auaray.network.CurrentWeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient{
        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder().apply {
            addInterceptor(interceptor) }
            .build()
    }
    @Provides
    @Singleton
    fun provideMoshi() =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    @Provides
    @Singleton
    fun provideCurrentWeatherApi(
        moshi: Moshi,
        client: OkHttpClient
    ) =  Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .build()
            .create(CurrentWeatherApiService::class.java)

}