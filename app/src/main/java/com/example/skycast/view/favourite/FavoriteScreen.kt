package com.example.skycast.view.favourite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.skycast.data.local.FavoriteLocationEntity
import com.example.skycast.viewModel.FavoriteViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun FavoriteScreen(viewModel: FavoriteViewModel) {
    val favoriteLocations by viewModel.favorites.collectAsState()
    var showMap by remember { mutableStateOf(false) }

    if (showMap) {
        MapScreen(
            onLocationSelected = { location ->
                viewModel.addFavorite(location)
                showMap = false
            },
            onDismiss = { showMap = false }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showMap = true },
                    containerColor = Color(0xFF38BDF8),
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Favorite")
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (favoriteLocations.isEmpty()) {
                    Text(
                        text = "No favorite locations added.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.headlineMedium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(favoriteLocations) { location ->
                            FavoriteItem(
                                location,
                                onRemove = viewModel::removeFavorite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(
    location: FavoriteLocationEntity,
    onRemove: (FavoriteLocationEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = location.name,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = { onRemove(location) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

@Composable
fun MapScreen(onLocationSelected: (FavoriteLocationEntity) -> Unit, onDismiss: () -> Unit) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            onMapClick = { latLng ->
                selectedLocation = latLng
            }
        ) {
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Selected Location"
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(Color.Gray)) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.height(8.dp))

            selectedLocation?.let { latLng ->
                Button(onClick = {
                    val locationEntity = FavoriteLocationEntity(
                        name = "Selected Place", // You can implement reverse geocoding for a real name
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )
                    onLocationSelected(locationEntity)
                }) {
                    Text("Save Location")
                }
            }
        }
    }
}
