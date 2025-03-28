package com.example.skycast.view.favouriteScreen

import android.content.Context
import android.location.Geocoder
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import com.example.skycast.data.local.FavoriteLocationEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MapScreen(onLocationSelected: (FavoriteLocationEntity) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationName by remember { mutableStateOf("Selected Location") }
    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()

    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar with AutoComplete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))

                AndroidView(
                    modifier = Modifier.weight(1f),
                    factory = { context ->
                        AutoCompleteTextView(context).apply {
                            setSingleLine(true)
                            imeOptions = EditorInfo.IME_ACTION_SEARCH
                            setAdapter(ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, suggestions))

                            setOnItemClickListener { _, _, position, _ ->
                                if (suggestions.isNotEmpty() && position >= 0 && position < suggestions.size) {
                                    val selectedPlace = suggestions[position]
                                    searchQuery = selectedPlace

                                    coroutineScope.launch(Dispatchers.IO) {
                                        val location = getLocationFromName(context, selectedPlace)
                                        if (location != null) {
                                            selectedLocation = location
                                            locationName = getLocationName(context, location.latitude, location.longitude)

                                            coroutineScope.launch(Dispatchers.Main) {
                                                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 8f))
                                            }
                                        }
                                    }
                                }
                            }

                            setOnEditorActionListener { _, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val location = getLocationFromName(context, text.toString())
                                        if (location != null) {
                                            selectedLocation = location
                                            locationName = getLocationName(context, location.latitude, location.longitude)

                                            coroutineScope.launch(Dispatchers.Main) {
                                                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 12f))
                                            }
                                        }
                                    }
                                }
                                false
                            }

                            doAfterTextChanged { newText ->
                                newText?.let {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val newSuggestions = getAutoCompleteSuggestions(context, it.toString())
                                        coroutineScope.launch(Dispatchers.Main) {
                                            suggestions = newSuggestions
                                            (adapter as? ArrayAdapter<String>)?.clear()
                                            (adapter as? ArrayAdapter<String>)?.addAll(newSuggestions)
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }

            // Google Map
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLocation = latLng
                    locationName = getLocationName(context, latLng.latitude, latLng.longitude)

                    // Move camera to clicked location
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                }
            ) {
                selectedLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = locationName
                    )
                }
            }
        }

        // Save & Cancel Buttons
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

// Get location from name
fun getLocationFromName(context: Context, name: String): LatLng? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(name, 5)
            ?.filter { address -> address.getAddressLine(0)?.contains(name, ignoreCase = true) == true }

        addresses?.firstOrNull()?.let {
            LatLng(it.latitude, it.longitude)
        }
    } catch (e: Exception) {
        null
    }
}

// Get location name from lat & lng
fun getLocationName(context: Context, lat: Double, lng: Double): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        addresses?.firstOrNull()?.let { address ->
            listOfNotNull(
                address.locality,
                address.subLocality,
                address.adminArea,
                address.countryName
            ).firstOrNull() ?: "Unknown Location"
        } ?: "Unknown Location"
    } catch (e: Exception) {
        "Unknown Location"
    }
}

// Get autocomplete suggestions
fun getAutoCompleteSuggestions(context: Context, query: String): List<String> {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(query, 5) ?: emptyList()
        addresses.mapNotNull { it.getAddressLine(0) }
    } catch (e: Exception) {
        emptyList()
    }
}
