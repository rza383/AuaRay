package kz.rza383.domain.repository

import kz.rza383.auaray.data.CurrentWeather
import kz.rza383.auaray.data.ForecastResponse
import retrofit2.http.Query

interface MyRepository  {
    suspend fun getCurrentWeather(
        latitude: Float,
        longitude: Float,
        uvIndex: String,
        precipitationChance: String,
        isCurrentWeather: String,
        forecastDays: String,
        auto: String
    ): CurrentWeather

    suspend fun getForecast(latitude: Float,
                            longitude: Float,
                            dailyParams: Array<String>,
                            forecastDays: String,
                            auto: String): ForecastResponse
}