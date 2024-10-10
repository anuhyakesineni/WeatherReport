package com.example.weatherreport.factory

// Import necessary classes for ViewModel and repository handling
import com.example.weatherreport.repository.WeatherRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherreport.viewmodel.WeatherViewModel

// Factory class to create instances of WeatherViewModel with a repository
class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {

    // Override the create method to provide WeatherViewModel instances
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is WeatherViewModel
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            // Return a new instance of WeatherViewModel with the provided repository
            return WeatherViewModel(repository) as T
        }
        // If the ViewModel class is unknown, throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
