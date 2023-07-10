package kz.rza383.domain

data class WeatherToday(
    val location: String,
    val temperature: Double,
    val elevation: Int,
    val uvIndex: Int,
    val windSpeed: Double,
    val rain: Int,
    val isDay: Int
)