package kz.rza383.auaray.network

import retrofit2.http.GET
import retrofit2.http.Query


interface CurrentWeatherApiService {
    @GET("/v1/forecast?")
    suspend fun getCurrentWeather(@Query("latitude") latitude: Float,
                                  @Query("longitude") longitude: Float,
                                  @Query("daily") uvIndex: String,
                                  @Query("daily") precipitationChance: String,
                                  @Query("current_weather") isCurrentWeather: Boolean,
                                  @Query("forecast_days") forecastDays: String,
                                  @Query("timezone") auto: String): CurrentWeather
    @GET("/v1/forecast?")
    suspend fun getForecast(@Query("latitude") latitude: Float,
                            @Query("longitude") longitude: Float,
                            @Query("daily") dailyParams: Array<String>,
                            @Query("forecast_days") forecastDays: String,
                            @Query("timezone") auto: String): ForecastResponse
}

