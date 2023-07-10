package kz.rza383.auaray.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kz.rza383.auaray.data.WeatherItem
import androidx.lifecycle.map
import kz.rza383.auaray.data.database.CurrentWeatherEntity
import kz.rza383.auaray.data.database.WeatherDao
import kz.rza383.auaray.di.IoDispatcher
import kz.rza383.domain.WeatherToday
import javax.inject.Inject
private const val TAG = "Db"

class DbRepository @Inject constructor(
    private val dao: WeatherDao,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher
) {

    val today: LiveData<WeatherToday> =  dao.getCurrentWeather().map { input ->
            WeatherToday(
                location = input.locationName,
                temperature = input.temperature,
                elevation = input.elevation,
                uvIndex = input.uvIndex.toInt(),
                windSpeed = input.windSpeed,
                rain = input.chanceOfPrecipitation,
                isDay = input.isDay
            )
        }


    suspend fun saveWeather(weather: CurrentWeatherEntity) =
        dao.insert(weather)
}