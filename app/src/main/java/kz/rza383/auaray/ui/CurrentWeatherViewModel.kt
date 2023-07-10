package kz.rza383.auaray.ui


import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kz.rza383.auaray.data.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kz.rza383.auaray.R
import kz.rza383.auaray.data.ForecastWeather
import kz.rza383.auaray.data.WeatherItem
import kz.rza383.auaray.data.repository.MyRepositoryImpl
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "viewmodel"
private const val UV_INDEX= "uv_index_max"
private const val PRECIPITATION_CHANCE = "precipitation_probability_max"
private const val TimeZone = "auto"

enum class WeatherApiStatus { LOADING, ERROR, DONE }

enum class UvIndex(val stringReference: Int) {
    LOW(R.string.low),
    MODERATE(R.string.moderate),
    HIGH(R.string.high),
    VERYHIGH(R.string.veryHigh),
    EXTREME(R.string.extreme)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val currentLocationTask: Task<Location>,
    private val gcd: Geocoder,
    private val repository: MyRepositoryImpl): ViewModel() {

    private val dailyParams = arrayOf("temperature_2m_max", "temperature_2m_min", "sunrise", "sunset", "precipitation_probability_max")
    private val lowRange = 1..2
    private val moderateRange = 3..5
    private val highRange = 6..7
    private val veryHighRange = 8..10
    private val _status = MutableLiveData<WeatherApiStatus>()
    val status: LiveData<WeatherApiStatus> get() = _status
    private val _currentWeather = MutableStateFlow<WeatherData>(WeatherData(0.0, 0.0,0,0,""))
    private val _temperature = MutableStateFlow(0.0)
    val temperature: StateFlow<Double> get() = _temperature
    private val _elevation = MutableStateFlow(0)
    val elevation: StateFlow<Int> get() = _elevation
    private val _windSpeed = MutableStateFlow(0.0)
    val windSpeed: StateFlow<Double> get() = _windSpeed
    private val _weatherCode = MutableStateFlow(0)
    val weatherCode: StateFlow<Int> get() = _weatherCode
    val isLoading = ObservableBoolean()
    private val _locationName = MutableLiveData<String>()
    val locationName: LiveData<String> get() = _locationName
    val todaysUvIndex = ObservableInt()
    private val _chanceOfRain2day = MutableStateFlow(0)
    val chanceOfRain2day: StateFlow<Int> get() = _chanceOfRain2day
    private val _isDay = MutableStateFlow(0)
    val isDay: StateFlow<Int> get() = _isDay
    private val forecast = MutableLiveData<ForecastWeather>()
    private val _set = MutableLiveData<LineDataSet>()
    val set: LiveData<LineDataSet> get() = _set
    private val _weatherListData = MutableLiveData<List<WeatherItem>>()
    val weatherListData: LiveData<List<WeatherItem>> get() = _weatherListData
    private var latitudeValue: Float? = null
    private var longitudeValue: Float? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getLocation() {
        currentLocationTask.addOnCompleteListener { task: Task<Location> ->
             if (task.isSuccessful) {
                with(task.result){
                    latitudeValue = latitude.toFloat()
                    longitudeValue = longitude.toFloat()
                    getCurrentWeather()
                }
                 getLocationName()
            } else {
                val exception = task.exception
                 Log.d(TAG, "getCurrentLocation() result: $exception")
            }
        }
    }


    private fun getLocationName() {
        val gcdListener = @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
                _locationName.postValue(addresses.first().locality.toString())
                Log.d(TAG, "geocoding value: $locationName")
            }
            override fun onError(errorMessage: String?) {
                super.onError(errorMessage)
                Log.d(TAG, errorMessage!!)
            }
        }
        if (Build.VERSION.SDK_INT >= 33)
            gcd.getFromLocation(latitudeValue!!.toDouble(),
                                longitudeValue!!.toDouble(),
                        1,
                                gcdListener)
        else {
            val addresses = gcd.getFromLocation(latitudeValue!!.toDouble(),
                                                longitudeValue!!.toDouble(),
                                                1)
            _locationName.postValue(addresses!!.first().locality.toString())
        }
    }

    private suspend fun getDataFromApi(){
        val apiCallResult = repository
            .getCurrentWeather(latitudeValue!!,
                longitudeValue!!,
                UV_INDEX,
                PRECIPITATION_CHANCE,
                "true",
                "1",
                TimeZone)
        _currentWeather.value = apiCallResult.listOfWeatherData
        _elevation.value = apiCallResult.elevation
        _currentWeather.value.apply {
            _temperature.value = temperature
            _windSpeed.value = windSpeed
            _weatherCode.value = weatherCode
            _isDay.value = isDay
        }
        apiCallResult.extraData.apply {
            getUvIndex(uvIndex.first().toInt())
            _chanceOfRain2day.value = precipitationChance.first()
        }
    }

    private suspend fun getForecastFromApi(){
        val apiCallResult = repository
            .getForecast(latitudeValue!!,
                longitudeValue!!,
                dailyParams,
                "7",
                TimeZone)
        Log.d(TAG, "${apiCallResult.forecast}")
        forecast.value = apiCallResult.forecast
        forecast.value?.let {
            constructListData(
                it.time,
                it.tempMax,
                it.tempMin,
                it.sunset,
                it.sunrise,
                it.chanceOfPrecipitationMax
            )
        _set.postValue(prepareDataForChart())

        }

    }

    private fun prepareDataForChart(): LineDataSet {
        val dataSet = mutableListOf<Entry>()
        val days = forecast.value?.time?.map { date ->
                date.split("-").last().toFloat() }

        val temperatureList = forecast.value?.tempMax?.map { t -> t.toFloat() }
        if (days != null && temperatureList != null) {
            for (i in days.indices) {
                dataSet.add(Entry(days[i], temperatureList[i]))
            }
        }
        return LineDataSet(dataSet, repository.chartDescription)
    }
    fun getForecast(){
        viewModelScope.launch {
            _status.value = WeatherApiStatus.LOADING
            try {
                getForecastFromApi()
                _status.value = WeatherApiStatus.DONE
            } catch (e: Exception){
                _status.value = WeatherApiStatus.ERROR
                Log.d(TAG, e.message!!)
            }
        }
    }
    private fun getCurrentWeather(){
        viewModelScope.launch {
            _status.value = WeatherApiStatus.LOADING
           try {
               getDataFromApi()
               _status.value = WeatherApiStatus.DONE
           } catch (e: Exception){
               _status.value = WeatherApiStatus.ERROR
               Log.d(TAG, e.message!!)
           }
        }
    }

    fun refreshCurrentWeatherFragment(){
        viewModelScope.launch {
            isLoading.set(true)
            try {
                getDataFromApi()
                isLoading.set(false)
            } catch (e: Exception){
                isLoading.set(false)
            }
        }
    }
    private fun constructListData(
        time: List<String>,
        tempMaxList: List<Double>,
        tempMinList: List<Double>,
        sunsetTime: List<String>,
        sunriseTime: List<String>,
        chanceOfRainList: List<Int>) {
        val dateInputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeInputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val formattedDay = time.map {
            val date = LocalDate.parse(it, dateInputFormat)
            "${date.dayOfMonth.toString().padStart(2,'0')}-${date.monthValue.toString().padStart(2,'0')}"
        }
        val formattedSunsetTime = sunsetTime.map {
            LocalTime.parse(it, timeInputFormat).toString() }
        val formattedSunriseTime = sunriseTime.map {
            LocalTime.parse(it, timeInputFormat).toString()
        }
        Log.d(TAG, "value of list ${formattedDay.joinToString (" ")}")
        Log.d(TAG, "value of list ${tempMaxList.joinToString (" ")}")
        Log.d(TAG, "value of list ${tempMinList.joinToString (" ")}")
        Log.d(TAG, "value of list ${formattedSunriseTime.joinToString (" ")}")
        Log.d(TAG, "value of list ${formattedSunsetTime.joinToString (" ")}")
        Log.d(TAG, "value of list ${chanceOfRainList.joinToString (" ")}")
        val listOfForecasts = mutableListOf<WeatherItem>()
        for(i in time.indices){
            listOfForecasts.add(
                WeatherItem(
                    formattedDay[i],
                    tempMaxList[i].toInt(),
                    tempMinList[i].toInt(),
                    formattedSunriseTime[i],
                    formattedSunsetTime[i],
                    chanceOfRainList[i]
                )
            )
        }
        _weatherListData.value = listOfForecasts
        Log.d(TAG, "value of list ${_weatherListData.value?.joinToString (" ") }}")
    }
    private fun getUvIndex(uvIndex: Int) {
        when (uvIndex) {
            in lowRange -> todaysUvIndex.set(UvIndex.LOW.stringReference)
            in highRange -> todaysUvIndex.set(UvIndex.HIGH.stringReference)
            in veryHighRange -> todaysUvIndex.set(UvIndex.VERYHIGH.stringReference)
            in moderateRange -> todaysUvIndex.set(UvIndex.MODERATE.stringReference)
            else -> todaysUvIndex.set(UvIndex.EXTREME.stringReference)
        }
    }
}