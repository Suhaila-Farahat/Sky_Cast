package com.example.skycast.view.favouriteScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.skycast.R
import com.example.skycast.data.local.fav.FavoriteLocationEntity
import com.example.skycast.viewModel.FavoriteViewModel

@Composable
fun FavoriteScreen(viewModel: FavoriteViewModel, onLocationClick: (FavoriteLocationEntity) -> Unit) {
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
                    containerColor = Color(0xFFF2F4F6),
                    contentColor = Color.Black,
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add To Favorite")
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF1E293B))
            ) {
                if (favoriteLocations.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.no_favorite_locations),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
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
                                onRemove = viewModel::removeFavorite,
                                onClick = { onLocationClick(location) }
                            )
                        }
                    }
                }
            }
        }
    }
}

