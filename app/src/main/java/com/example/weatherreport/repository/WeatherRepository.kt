package com.example.weatherreport.repository

// Importing necessary components
import com.example.weatherreport.entity.WeatherResponse
import com.example.weatherreport.remote.RetrofitInstance
import com.example.weatherreport.remote.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repository class responsible for handling data operations related to weather
class WeatherRepository() {

    // Suspend function to fetch weather data from the remote API
    suspend fun getWeather(city: String, apiKey: String): WeatherResponse {
        // Switches the coroutine context to IO (Input/Output) for network call
        return withContext(Dispatchers.IO) {
            // Makes the API call using Retrofit and returns the weather response
            RetrofitInstance.api.getWeather(city, apiKey)
        }
    }

    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double, apiKey: String): WeatherResponse {
        return RetrofitInstance.api.getWeatherByCoordinates(latitude, longitude, apiKey)
    }
}