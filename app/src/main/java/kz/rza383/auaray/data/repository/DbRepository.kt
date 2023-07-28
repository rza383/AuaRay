package kz.rza383.auaray.data.repository

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kz.rza383.auaray.data.database.CurrentWeatherEntity
import kz.rza383.auaray.data.database.WeatherDao
import kz.rza383.auaray.di.IoDispatcher
import kz.rza383.auaray.util.Constants
import kz.rza383.domain.WeatherToday
import javax.inject.Inject

private const val TAG = "Db"

class DbRepository @Inject constructor(
    private val dao: WeatherDao,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val sharedPreferences: SharedPreferences,
    private val sharedPreferencesEditor: Editor
) {


    suspend fun saveWeather(weather: CurrentWeatherEntity) =
        dao.insert(weather)

    suspend fun getWeather() = dao.getCurrentWeather()

    val today = dao.getCurrentWeather().filterNotNull().map { input ->
                    WeatherToday(
                        temperature = input.temperature,
                        elevation = input.elevation,
                        uvIndex = input.uvIndex.toInt(),
                        windSpeed = input.windSpeed,
                        rain = input.chanceOfPrecipitation,
                        isDay = input.isDay,
                        location = input.locationName
                    )
                }

    fun updateSharedPreferences(isChecked: Boolean){
        if(isChecked)
            sharedPreferencesEditor.putBoolean(Constants.SWITCH_KEY, true).apply()
        else
            sharedPreferencesEditor.putBoolean(Constants.SWITCH_KEY, false).apply()

    }

    fun returnSavedTheme() =
        sharedPreferences.getBoolean(Constants.SWITCH_KEY, true)

}