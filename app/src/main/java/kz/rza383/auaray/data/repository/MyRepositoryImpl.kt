package kz.rza383.auaray.data.repository

import android.app.Application
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import dagger.Lazy
import kz.rza383.auaray.R
import kz.rza383.auaray.data.CurrentWeather
import kz.rza383.auaray.data.ForecastResponse
import kz.rza383.auaray.network.CurrentWeatherApiService
import kz.rza383.domain.repository.MyRepository
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(
    private val api: Lazy<CurrentWeatherApiService>,
    private val appContext: Application
): MyRepository {

    val chartDescription =
        appContext.resources.getString(
            R.string.chart_description)
    override suspend fun getCurrentWeather(
        latitude: Float,
        longitude: Float,
        uvIndex: String,
        precipitationChance: String,
        isCurrentWeather: String,
        forecastDays: String,
        auto: String
    ): CurrentWeather =
        api
            .get()
            .getCurrentWeather(
            latitude,
            longitude,
            uvIndex,
            precipitationChance,
            isCurrentWeather,
            forecastDays,
            auto
        )

    override suspend fun getForecast(
        latitude: Float,
        longitude: Float,
        dailyParams: Array<String>,
        forecastDays: String,
        auto: String
    ): ForecastResponse =
        api
            .get()
            .getForecast(
                latitude,
                longitude,
                dailyParams,
                forecastDays,
                auto
            )

}