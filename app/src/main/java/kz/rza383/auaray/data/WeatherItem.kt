package kz.rza383.auaray.data

data class WeatherItem (
    val date: String,
    val tempMax: Int,
    val tempMin: Int,
    val sunriseTime: String,
    val sunsetTime: String,
    val chanceOfRain: Int
    )