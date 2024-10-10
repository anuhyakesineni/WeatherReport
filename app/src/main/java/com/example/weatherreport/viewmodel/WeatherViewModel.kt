package com.example.weatherreport.viewmodel

// Importing required Android and Kotlin libraries
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherreport.entity.WeatherResponse
import com.example.weatherreport.repository.WeatherRepository
import kotlinx.coroutines.launch


// The ViewModel class for managing weather data
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    // MutableLiveData to hold the weather data fetched from the repository
    val weatherData = MutableLiveData<WeatherResponse>()

    // MutableLiveData to hold any error messages that occur while fetching data
    val errorMessage = MutableLiveData<String?>()

    // Function to fetch weather data based on the city and API key
    fun getWeather(city: String, apiKey: String) {
        // Launch a coroutine in the ViewModel scope
        viewModelScope.launch {
            try {
                // Fetch the weather data from the repository
                val result = repository.getWeather(city, apiKey)

                // Update the weatherData LiveData with the result
                weatherData.value = result
            } catch (e: Exception) {
                // In case of any error, update the errorMessage LiveData
                errorMessage.value = "Error fetching weather data"
            }
        }
    }


    fun getWeatherByCoordinates(latitude: Double, longitude: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val result = repository.getWeatherByCoordinates(latitude, longitude, apiKey)
                weatherData.value = result
            } catch (e: Exception) {
                errorMessage.value = "Error fetching weather data"
            }
        }
    }
    fun clearErrorMessage(){
        errorMessage.value =null
    }
}