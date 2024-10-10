package com.example.weatherreport.entity

// Data class representing the response from the weather API
data class WeatherResponse(
    val name: String, // The name of the city
    val main: Main, // Nested object representing main weather details like temperature and humidity
    val weather: List<Weather> // A list containing weather conditions (e.g., description, icon)
)

// Data class representing the main weather attributes
data class Main(
    val temp: Double, // Temperature in Kelvin (or specified unit)
    val humidity: Int // Humidity percentage
)

// Data class representing weather condition details
data class Weather(
    val description: String, // A brief description of the weather (e.g., "clear sky")
    val icon: String // Icon ID to represent weather visually (e.g., "01d" for sunny)
)