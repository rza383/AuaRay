package kz.rza383.auaray.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kz.rza383.domain.WeatherToday

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: CurrentWeatherEntity)

    @Update
    suspend fun update(weather: CurrentWeatherEntity)

    @Delete
    suspend fun delete(weather: CurrentWeatherEntity)

    @Query("select * from `today's_weather` order by time desc limit 1")
    fun getCurrentWeather(): LiveData<CurrentWeatherEntity>?
}