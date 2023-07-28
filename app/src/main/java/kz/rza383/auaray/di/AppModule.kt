package kz.rza383.auaray.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.rza383.auaray.data.database.PREPOPULATE_DATA
import kz.rza383.auaray.data.database.WeatherDao
import kz.rza383.auaray.util.Constants
import kz.rza383.auaray.data.database.WeatherDb
import kz.rza383.auaray.network.CurrentWeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Locale
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Volatile
    private var INSTANCE: WeatherDb? = null

    object WeatherDbCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.weatherDao().insert(PREPOPULATE_DATA)
                }
            }
        }
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder().apply {
            addInterceptor(interceptor) }
            .cache(null)
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
    fun provideGeocoder(
        @ApplicationContext context: Context
    ) = Geocoder(context, Locale.getDefault())

    @OptIn(InternalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideDb(
        @ApplicationContext context: Context
    ): WeatherDb {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context,
                WeatherDb::class.java,
                "weather_database")
                .addCallback(WeatherDbCallback)
                .build()
                .also { INSTANCE = it }
            instance
        }
    }

    @Provides
    @Singleton
    fun provideDao(db: WeatherDb): WeatherDao = db.weatherDao()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences =
        appContext.getSharedPreferences(Constants.PREF_KEY, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideSharedPreferencesEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor =
        sharedPreferences.edit()


}