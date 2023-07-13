package kz.rza383.auaray.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrentWeatherEntity::class], version = 8)
abstract class WeatherDb: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}