package kz.rza383.auaray.di

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kz.rza383.auaray.util.Constants
import kz.rza383.auaray.data.database.WeatherDb
import kz.rza383.auaray.network.CurrentWeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Locale
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder().apply {
            addInterceptor(interceptor) }
            .build()
    }
    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    @Provides
    @Singleton
    fun provideCurrentWeatherApi(
        moshi: Moshi,
        client: OkHttpClient
    ): CurrentWeatherApiService =  Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .build()
            .create(CurrentWeatherApiService::class.java)

    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ) = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    @SuppressLint("MissingPermission")
    fun provideCurrentLocationTask(
        fusedLocationClient: Lazy<FusedLocationProviderClient>
    ) = fusedLocationClient
        .get()
        .getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        CancellationTokenSource().token
    )

    @Provides
    @Singleton
    fun provideGeocoder(
        @ApplicationContext context: Context
    ) = Geocoder(context, Locale.getDefault())

    @Provides
    @Singleton
    fun provideDb(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context,
        WeatherDb::class.java,
        "weather_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideDao(db: WeatherDb) = db.weatherDao()


}