package kz.rza383.auaray.data.database

import androidx.annotation.NonNull
import androidx.constraintlayout.motion.widget.FloatLayout
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "today's_weather")
data class CurrentWeatherEntity (
    @PrimaryKey
    val time: String,
    @ColumnInfo(name = "name_of_location")
    val locationName: String,
    val temperature: Double,
    val elevation: Int,
    @ColumnInfo(name = "wind_speed")
    val windSpeed: Double,
    @ColumnInfo(name = "uv_index")
    val uvIndex: Double,
    @ColumnInfo(name = "precipitation_probability")
    val chanceOfPrecipitation: Int,
    @ColumnInfo(name = "day_or_night")
    val isDay: Int
)

val PREPOPULATE_DATA = CurrentWeatherEntity(
    "1992-06-03",
    "",
    0.0,
    0,
    0.0,
    0.0,
    0,
    1
)