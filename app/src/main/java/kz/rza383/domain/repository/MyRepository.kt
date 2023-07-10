package kz.rza383.domain.repository

import kz.rza383.auaray.network.CurrentWeather
import kz.rza383.auaray.network.ForecastResponse

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