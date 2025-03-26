package com.example.skycast.view


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.skycast.R

@Composable
fun BottomNavBar() {
    BottomAppBar(
        containerColor = Color(0xFF1E2A47),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { /* Navigate to Home */ }) {
                Icon(painterResource(R.drawable.ic_home), contentDescription = "Home", tint = Color.White)
            }
            IconButton(onClick = { /* Navigate to Search */ }) {
                Icon(painterResource(R.drawable.ic_home), contentDescription = "Search", tint = Color.White)
            }
            IconButton(onClick = { /* Navigate to Forecast */ }) {
                Icon(painterResource(R.drawable.ic_home), contentDescription = "Forecast", tint = Color.White)
            }
            IconButton(onClick = { /* Navigate to Settings */ }) {
                Icon(painterResource(R.drawable.ic_home), contentDescription = "Settings", tint = Color.White)
            }
        }
    }
}
