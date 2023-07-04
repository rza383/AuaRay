package kz.rza383.auaray.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kz.rza383.auaray.data.CurrentWeather
import kz.rza383.auaray.data.ForecastResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
val client : OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(interceptor) }
    .build()
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private const val BASE_URL =
    "https://api.open-meteo.com"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()
interface CurrentWeatherApiService {
    @GET("/v1/forecast?")
    suspend fun getCurrentWeather(@Query("latitude") latitude: Float,
                                  @Query("longitude") longitude: Float,
                                  @Query("daily") uvIndex: String,
                                  @Query("daily") precipitationChance: String,
                                  @Query("current_weather") isCurrentWeather: String,
                                  @Query("forecast_days") forecastDays: String,
                                  @Query("timezone") auto: String): CurrentWeather
    @GET("/v1/forecast?")
    suspend fun getForecast(@Query("latitude") latitude: Float,
                            @Query("longitude") longitude: Float,
                            @Query("daily") dailyParams: Array<String>,
                            @Query("forecast_days") forecastDays: String,
                            @Query("timezone") auto: String): ForecastResponse
}

object CurrentWeatherApi {
    val retrofitService: CurrentWeatherApiService by lazy {
        retrofit.create(CurrentWeatherApiService::class.java)
    }
}