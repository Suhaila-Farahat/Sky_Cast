@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package com.example.skycast.view.navigation

import com.example.skycast.R
import kotlinx.serialization.Serializable

@Serializable
sealed class BottomBarRoutes(
    val title: String,
    val icon: Int
) {
    @Serializable
    object Home : BottomBarRoutes("Home", R.drawable.ic_home)

    @Serializable
    object Favorites : BottomBarRoutes("Favorites", R.drawable.ic_fav)

    @Serializable
    object WeatherAlerts : BottomBarRoutes("Alerts", R.drawable.ic_home)

    @Serializable
    object Settings : BottomBarRoutes("Settings", R.drawable.ic_home)
}
