package com.example.skycast.view.favouriteScreen

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.skycast.data.local.FavoriteLocationEntity
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import java.util.Locale

@Composable
fun MapScreen(onLocationSelected: (FavoriteLocationEntity) -> Unit, onDismiss: () -> Unit) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationName by remember { mutableStateOf("Selected Location") }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            onMapClick = { latLng ->
                selectedLocation = latLng
                locationName = getLocationName(context, latLng.latitude, latLng.longitude)
            }
        ) {
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = locationName
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
                        name = locationName,
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )
                    onLocationSelected(locationEntity)
                }) {
                    Text("Save $locationName")
                }
            }
        }
    }
}

fun getLocationName(context: Context, lat: Double, lng: Double): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        addresses?.firstOrNull()?.locality ?: "Unknown Location"
    } catch (e: Exception) {
        "Unknown Location"
    }
}