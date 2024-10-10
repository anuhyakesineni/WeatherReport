package com.example.weatherreport.remote

// Importing necessary components from Retrofit and the WeatherResponse entity
import com.example.weatherreport.entity.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Interface defining the Weather API service for Retrofit
interface WeatherApiService {

    // API endpoint for fetching weather data
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String, // The city name for which to fetch the weather
        @Query("appid") apiKey: String, // API key for authentication
        @Query("units") units: String = "metric" // Default unit for temperature (Celsius)
    ): WeatherResponse // The expected response type from the API

    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // Optional: Set units to metric
    ): WeatherResponse
}