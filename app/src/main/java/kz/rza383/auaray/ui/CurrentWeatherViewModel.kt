package kz.rza383.auaray.ui

import android.annotation.SuppressLint
import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kz.rza383.auaray.data.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kz.rza383.auaray.R
import kz.rza383.auaray.network.CurrentWeatherApi
import java.util.Locale

private const val TAG = "viewmodel"
private const val UV_INDEX= "uv_index_max"
private const val PRECIPITATION_CHANCE = "precipitation_probability_max"

enum class WeatherApiStatus { LOADING, ERROR, DONE }

enum class UvIndex(val stringReference: Int) {
    LOW(R.string.low),
    MODERATE(R.string.moderate),
    HIGH(R.string.high),
    VERYHIGH(R.string.veryHigh),
    EXTREME(R.string.extreme)
}

class CurrentWeatherViewModel(application: Application): AndroidViewModel(application) {
    private val app = application
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(app)
    }
    private val lowRange = 1..2
    private val moderateRange = 3..5
    private val highRange = 6..7
    private val veryHighRange = 8..10
    private val _status = MutableLiveData<WeatherApiStatus>()
    val status: LiveData<WeatherApiStatus> get() = _status
    private var cancellationTokenSource = CancellationTokenSource()
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
    private var latitudeValue: Float? = null
    private var longitudeValue: Float? = null


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getLocation() {
        @SuppressLint("MissingPermission")
        val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
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
        val gcd = Geocoder(app, Locale.getDefault())
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
        val apiCallResult = CurrentWeatherApi
            .retrofitService
            .getCurrentWeather(latitudeValue!!,
                longitudeValue!!,
                UV_INDEX,
                PRECIPITATION_CHANCE,
                "true",
                "1",
                "auto")
        _status.value = WeatherApiStatus.DONE
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
        Log.d(TAG, "uv val ${todaysUvIndex}")
        Log.d(TAG, "${todaysUvIndex}, ${chanceOfRain2day.value}")
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

    private fun getUvIndex(uvIndex: Int) {
        when (uvIndex) {
            in lowRange -> todaysUvIndex.set(UvIndex.LOW.stringReference)
            in highRange -> todaysUvIndex.set(R.string.low)
            in veryHighRange -> todaysUvIndex.set(UvIndex.VERYHIGH.stringReference)
            in moderateRange -> todaysUvIndex.set(UvIndex.MODERATE.stringReference)
            else -> todaysUvIndex.set(UvIndex.EXTREME.stringReference)
        }
    }

}