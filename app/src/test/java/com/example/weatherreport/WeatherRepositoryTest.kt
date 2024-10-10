package com.example.weatherreport

// Import necessary components for testing, mocking, and coroutines
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherreport.entity.Main
import com.example.weatherreport.entity.Weather
import com.example.weatherreport.entity.WeatherResponse
import com.example.weatherreport.remote.WeatherApiService
import com.example.weatherreport.repository.WeatherRepository
import com.example.weatherreport.util.Constants
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class WeatherRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule() // To ensure LiveData runs synchronously in tests

    // Mocking WeatherApiService to simulate API responses
    private val apiService = mock(WeatherApiService::class.java)

    // Instance of WeatherRepository to test
    private val repository = WeatherRepository()

    // Test to verify if getWeather returns the expected weather data
    @Test
    fun `getWeather returns weather data`() = runBlocking {
        // Arrange: Setup expected values and mock API behavior
        val city = "London"
        val apiKey = Constants.API_KEY
        val expectedWeather = WeatherResponse(
            name = "London",
            main = Main(temp = 15.0, humidity = 80),
            weather = listOf(Weather(description = "Clear", icon = "01d"))
        )

        // Simulating a successful API response
        `when`(apiService.getWeather(city, apiKey)).thenReturn(expectedWeather)

        // Act: Calling the method being tested
        val result = repository.getWeather(city, apiKey)

        // Assert: Verify the result matches the expected output
        assertEquals(expectedWeather, result)
    }

    // Test to verify if getWeather throws an exception when there's an error
    @Test(expected = Exception::class)
    fun `getWeather throws exception on error`(): Unit = runBlocking {
        // Arrange: Setup city and API key, mock the exception
        val city = "London"
        val apiKey = Constants.API_KEY
        `when`(apiService.getWeather(city, apiKey)).thenThrow(Exception("Network Error"))

        // Act & Assert: Call the method and expect an exception to be thrown
        repository.getWeather(city, apiKey)
    }
}
