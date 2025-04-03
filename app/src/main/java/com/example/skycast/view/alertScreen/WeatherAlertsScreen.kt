package com.example.skycast.view.alertScreen

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.skycast.data.local.alert.WeatherAlert
import com.example.skycast.viewModel.AlertViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherAlertsScreen(
    viewModel: AlertViewModel,
    modifier: Modifier = Modifier
) {
    val alerts by viewModel.alerts.collectAsState()
    var showAddAlertDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddAlertDialog = true },
                containerColor = Color(0xFFFDFDFD),
                contentColor = Color(0xFF0F172A)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Alert"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)))) // Gradient background
        ) {
            if (alerts.isEmpty()) {
                EmptyAlertsMessage(modifier = Modifier.align(Alignment.Center))
            } else {
                AlertsList(
                    alerts = alerts,
                    onDeleteAlert = { viewModel.removeAlert(it) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (showAddAlertDialog) {
            AddAlertDialog(
                onDismiss = { showAddAlertDialog = false },
                onAddAlert = { newAlert ->
                    viewModel.addAlert(newAlert)
                    showAddAlertDialog = false
                }
            )
        }
    }
}

@Composable
private fun EmptyAlertsMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No weather alerts",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White // Custom text color
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to create a new alert",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White // Custom text color
        )
    }
}

@Composable
private fun AlertsList(
    alerts: List<WeatherAlert>,
    onDeleteAlert: (WeatherAlert) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(alerts) { alert ->
            AlertItem(
                alert = alert,
                onDelete = { onDeleteAlert(alert) }
            )
        }
    }
}

@Composable
private fun AlertItem(
    alert: WeatherAlert,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val formattedDate = remember(alert.time) {
        dateFormat.format(Date(alert.time))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        )
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White // Custom text color
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Alert",
                        tint = MaterialTheme.colorScheme.error // Custom delete icon color
                    )
                }
            }

            // Main alert message centered vertically and horizontally
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text(
                    text = alert.alertMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White // Custom text color
                )
            }
        }
    }
}
