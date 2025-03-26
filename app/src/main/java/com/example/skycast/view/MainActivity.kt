package com.example.skycast.view

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.skycast.data.remote.RemoteDataSource
import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.repository.WeatherRepository
import com.example.skycast.viewmodel.HomeViewModel
import com.example.skycast.viewmodel.HomeViewModelFactory

class MainActivity : ComponentActivity() {


    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(application, WeatherRepository(RemoteDataSource(RetrofitClient.apiService)))
    }

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                homeViewModel.fetchWeather()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        setContent {
            MaterialTheme {
                HomeScreen(homeViewModel)
            }
        }
    }
}
