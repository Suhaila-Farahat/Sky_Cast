package com.example.skycast

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skycast.data.local.FavoriteDatabase
import com.example.skycast.data.local.FavoriteLocationEntity
import com.example.skycast.data.local.LocalDataSource
import com.example.skycast.data.remote.RemoteDataSource
import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.data.repo.WeatherRepository
import com.example.skycast.view.SettingsScreen
import com.example.skycast.view.WeatherAlertsScreen
import com.example.skycast.view.favouriteScreen.FavoriteScreen
import com.example.skycast.view.favouriteScreen.ForecastScreen
import com.example.skycast.view.homeScreen.HomeScreen
import com.example.skycast.view.navigation.BottomBarRoutes
import com.example.skycast.viewModel.FavoriteViewModel
import com.example.skycast.viewModel.FavoriteViewModelFactory
import com.example.skycast.viewModel.HomeViewModel
import com.example.skycast.viewModel.HomeViewModelFactory

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            application,
            WeatherRepository(
                RemoteDataSource(RetrofitClient.apiService),
                LocalDataSource(FavoriteDatabase.getDatabase(application).favoriteLocationDao())
            )
        )
    }

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteViewModelFactory(
            WeatherRepository(
                RemoteDataSource(RetrofitClient.apiService),
                LocalDataSource(FavoriteDatabase.getDatabase(application).favoriteLocationDao())
            )
        )
    }

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (isGranted) {
                homeViewModel.fetchWeather()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            MaterialTheme {
                MainScreen(homeViewModel, favoriteViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(homeViewModel: HomeViewModel, favoriteViewModel: FavoriteViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavigationGraph(navController, homeViewModel, favoriteViewModel, Modifier.padding(paddingValues))
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    favoriteViewModel: FavoriteViewModel,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarRoutes.Home.title,
        modifier = modifier
    ) {
        composable(BottomBarRoutes.Home.title) { HomeScreen(homeViewModel) }
        composable(BottomBarRoutes.Favorites.title) {
            FavoriteScreen(favoriteViewModel) { location ->
                Log.d("FavoriteScreen", "Navigating to Forecast with: ${location.name}, ${location.latitude}, ${location.longitude}")
                navController.navigate("forecast/${location.latitude}/${location.longitude}/${location.name}")
            }
        }
        composable(BottomBarRoutes.WeatherAlerts.title) { WeatherAlertsScreen() }
        composable(BottomBarRoutes.Settings.title) { SettingsScreen() }

        // Forecast screen route
        composable("forecast/{lat}/{lon}/{name}") { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0
            val name = backStackEntry.arguments?.getString("name") ?: "Unknown"

            val location = FavoriteLocationEntity(name = name, latitude = lat, longitude = lon)
            ForecastScreen(location, homeViewModel)
        }
    }
}



@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(
        BottomBarRoutes.Home,
        BottomBarRoutes.Favorites,
        BottomBarRoutes.WeatherAlerts,
        BottomBarRoutes.Settings
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            ),
        containerColor = Color.Transparent
    ) {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

        screens.forEach { screen ->
            NavigationBarItem(
                selected = currentDestination == screen.title,
                onClick = {
                    navController.navigate(screen.title) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.title,
                        tint =  Color.White
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        color =  Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (currentDestination == screen.title) FontWeight.Bold else FontWeight.Normal
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}
