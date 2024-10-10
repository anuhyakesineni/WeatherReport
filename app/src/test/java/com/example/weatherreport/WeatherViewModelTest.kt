package com.example.weatherreport

// Import necessary components for testing, mocking, and LiveData observation
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weatherreport.entity.Main
import com.example.weatherreport.entity.Weather
import com.example.weatherreport.entity.WeatherResponse
import com.example.weatherreport.repository.WeatherRepository
import com.example.weatherreport.util.Constants.Companion.API_KEY
import com.example.weatherreport.viewmodel.WeatherViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class WeatherViewModelTest {

    // Rule to ensure LiveData is tested synchronously
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Mock the WeatherRepository
    private val repository = mock(WeatherRepository::class.java)

    // Initialize the ViewModel with the mocked repository
    private val viewModel = WeatherViewModel(repository)

    // Test to verify that the weather data is correctly updated in LiveData
    @Test
    fun `getWeather updates weatherData LiveData`() = runBlocking {
        // Arrange: Define city, API key, and expected weather data
        val city = "London"
        val apiKey = API_KEY
        val expectedWeather = WeatherResponse(
            name = "London",
            main = Main(temp = 15.0, humidity = 80),
            weather = listOf(Weather(description = "Clear", icon = "01d"))
        )

        // Mock the repository's getWeather method to return the expected weather data
        `when`(repository.getWeather(city, apiKey)).thenReturn(expectedWeather)

        // Create an observer for the LiveData
        val observer = mock(Observer::class.java) as Observer<WeatherResponse>
        viewModel.weatherData.observeForever(observer)

        // Act: Call getWeather in the ViewModel
        viewModel.getWeather(city, apiKey)

        // Assert: Verify that the observer received the expected weather data
        verify(observer).onChanged(expectedWeather)
    }

    // Test to verify that errors are handled correctly and the error message is updated in LiveData
    @Test
    fun `getWeather handles error correctly`() = runBlocking {
        // Arrange: Define city and API key, mock the repository to throw an exception
        val city = "London"
        val apiKey = API_KEY
        `when`(repository.getWeather(city, apiKey)).thenThrow(Exception("Network Error"))

        // Create an observer for the error message LiveData
        val errorObserver = mock(Observer::class.java) as Observer<String?>
        viewModel.errorMessage.observeForever(errorObserver)

        // Act: Call getWeather in the ViewModel
        viewModel.getWeather(city, apiKey)

        // Assert: Verify that the observer received the expected error message
        verify(errorObserver).onChanged("Error fetching weather data")
    }
}
