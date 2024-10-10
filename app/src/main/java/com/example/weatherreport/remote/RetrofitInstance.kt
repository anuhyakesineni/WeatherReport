package com.example.weatherreport.remote

// Import necessary Retrofit components
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Singleton object to provide Retrofit instance for making API calls
object RetrofitInstance {

    // Base URL of the weather API
    private const val BASE_URL = "https://api.openweathermap.org/"

    // Lazy initialization of the Retrofit service instance
    val api: WeatherApiService by lazy {
        // Builds the Retrofit instance using the base URL and a Gson converter
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Sets the base URL for API requests
            .addConverterFactory(GsonConverterFactory.create()) // Adds Gson for JSON deserialization
            .build() // Builds the Retrofit instance
            .create(WeatherApiService::class.java) // Creates an implementation of the WeatherApiService
    }
}