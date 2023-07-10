package kz.rza383.auaray.network

import com.squareup.moshi.Json

data class ForecastResponse(
    @Json(name = "daily")
    val forecast: ForecastWeather
)

data class ForecastWeather (
    val time: List<String>,
    @Json(name = "temperature_2m_max")
    val tempMax: List<Double>,
    @Json(name = "temperature_2m_min")
    val tempMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @Json(name = "precipitation_probability_max")
    val chanceOfPrecipitationMax: List<Int>

)