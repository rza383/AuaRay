package kz.rza383.auaray.data

import com.squareup.moshi.Json

data class CurrentWeather(
    val elevation: Int,
    @Json(name = "current_weather")
    val listOfWeatherData: WeatherData,
    @Json(name = "daily")
    val extraData: Daily
)

data class WeatherData(
    val temperature: Double,
    @Json(name = "windspeed")
    val windSpeed: Double,
    @Json(name = "weathercode")
    val weatherCode: Int,
    @Json(name = "is_day")
    val isDay: Int,
    val time: String
)

data class Daily(
    val time: List<String>,
    @Json(name="uv_index_max")
    val uvIndex: List<Double>,
    @Json(name = "precipitation_probability_max")
    val precipitationChance: List<Int>
)
