package com.example.weatherreport

// Import necessary Android components, permissions, and location services
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.weatherreport.entity.WeatherResponse
import com.example.weatherreport.factory.WeatherViewModelFactory
import com.example.weatherreport.repository.WeatherRepository
import com.example.weatherreport.ui.theme.WeatherReportTheme
import com.example.weatherreport.util.Constants.Companion.API_KEY
import com.example.weatherreport.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class MainActivity : ComponentActivity() {

    // FusedLocationProviderClient for obtaining user's location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var sharedPref: SharedPreferences

    // Request permission launcher to handle location permission requests
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getLastLocation() // Get location if permission granted
            } else {
                // Handle permission denial
                Toast.makeText(this, getString(R.string.app_needs_the_permission), Toast.LENGTH_SHORT ).show()



            }
        }

    // ViewModel for managing weather data
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(WeatherRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize SharedPreferences
        sharedPref = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)


        // Initialize FusedLocationProviderClient for accessing location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val lastCity = sharedPref.getString("last_city", "")



        // Check if location permission is granted, if not, request it
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        } else {
            requestLocationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }



        enableEdgeToEdge() // Enables immersive UI experience
        setContent {
            WeatherReportTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherApp(viewModel = viewModel, lastCity = lastCity) // Calls the WeatherApp composable function

                }
            }
        }
    }

    // Function to retrieve the user's last known location
    private fun getLastLocation() {
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationTask: Task<Location> = fusedLocationClient.lastLocation
            locationTask.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Fetch weather data using the user's location
                    fetchWeatherData(latitude, longitude)
                } else {
                    // Handle the case where location is null
                }
            }
        }
    }

    private fun fetchWeatherDataByCity(city: String) {
        viewModel.getWeather(city, API_KEY)
    }


    // Function to fetch weather data based on latitude and longitude
    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModel.getWeatherByCoordinates(latitude, longitude, API_KEY)
    }
}

@Composable
fun WeatherApp(viewModel: WeatherViewModel,lastCity:String?) {
    // Observe LiveData for weather and error messages
    val weatherData by viewModel.weatherData.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()

    // State for storing user input for city name
    var cityInput by remember { mutableStateOf(TextFieldValue(lastCity?:"")) }

    val context = LocalContext.current

    // Fetch weather data when the lastCity is available
    LaunchedEffect(lastCity) {
        if (lastCity?.isNotEmpty() == true) {
            viewModel.getWeather(lastCity, API_KEY)
        }
    }

    // UI layout using Compose
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // TextField for entering city name
        BasicTextField(
            value = cityInput,
            onValueChange = { cityInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (cityInput.text.isEmpty()) {
                    Text("Enter city name", color = Color.Gray) // Placeholder text
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search button for fetching weather data based on city input
        Button(
            onClick = {
                if (cityInput.text.isNotEmpty()) {
                    val sharedPref = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("last_city", cityInput.text)
                        apply()
                    }

                    viewModel.getWeather(cityInput.text, API_KEY) // Fetch weather using city name
                }
            },
            modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.CenterHorizontally)
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display weather data if available
        weatherData?.let { weatherResponse ->
            WeatherInfoDisplay(weatherResponse)
        }

        // Display error message if available
        errorMessage?.let {
            Toast.makeText(context,errorMessage, Toast.LENGTH_SHORT ).show()
            viewModel.clearErrorMessage()
        }
    }
}

@Composable
fun WeatherInfoDisplay(weatherResponse: WeatherResponse) {
    // Layout to display weather details
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(text = "City: ${weatherResponse.name}")
        Text(text = "Temperature: ${weatherResponse.main.temp} °C")
        Text(text = "Description: ${weatherResponse.weather[0].description}")

        // Load and display weather icon using Coil
        val iconUrl = "http://openweathermap.org/img/wn/${weatherResponse.weather[0].icon}.png"
        Image(
            painter = rememberImagePainter(data = iconUrl),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Fit
        )
    }
}
