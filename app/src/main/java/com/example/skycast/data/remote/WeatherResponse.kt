package com.example.skycast.data.remote

import com.example.skycast.data.model.MainInfo
import com.example.skycast.data.model.SysInfo
import com.example.skycast.data.model.WeatherInfo
import com.example.skycast.data.model.WindInfo

data class WeatherResponse(
    val name: String,
    val weather: List<WeatherInfo>,
    val main: MainInfo,
    val wind: WindInfo,
    val sys: SysInfo
)
