package com.example.skycast

import android.content.SharedPreferences
import com.example.skycast.data.local.LocalDataSource
import com.example.skycast.data.local.home.WeatherEntity
import com.example.skycast.data.model.Clouds
import com.example.skycast.data.model.MainInfo
import com.example.skycast.data.model.SysInfo
import com.example.skycast.data.model.WeatherResponse
import com.example.skycast.data.model.WindInfo
import com.example.skycast.data.remote.RemoteDataSource
import com.example.skycast.data.repo.WeatherRepository
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setup() {
        remoteDataSource = mockk()
        localDataSource = mockk()
        sharedPreferences = mockk(relaxed = true)

        weatherRepository = WeatherRepository(remoteDataSource, localDataSource, sharedPreferences)
    }

    @After
    fun tearDown() {
        clearMocks(remoteDataSource, localDataSource, sharedPreferences)
    }

    @Test
    fun `getWeather fetches weather data from remote`() = runBlocking {
        val weatherResponse = WeatherResponse(
            weather = listOf(),
            main = MainInfo(temp = 25.0, pressure = 1015, humidity = 80),
            wind = WindInfo(speed = 5.0),
            clouds = Clouds(Clouds = 40),
            name = "Egypt",
            sys = SysInfo(sunset = 10, sunrise = 5)

        )

        coEvery { remoteDataSource.getCurrentWeather(12.34, 56.78, "apiKey") } returns weatherResponse

        val result = weatherRepository.getWeather(12.34, 56.78, "apiKey")

        assertEquals(weatherResponse, result)
        coVerify { remoteDataSource.getCurrentWeather(12.34, 56.78, "apiKey") }
    }


    @Test
    fun `cacheWeather caches weather data in local data source`() = runBlocking {
        val weatherEntity = WeatherEntity(
            id = 1,
            temp = 25.0,
            pressure = 1015,
            humidity = 80,
            windSpeed = 5.0,
            cityName = "Egypt",
            description = "cloudy",
            icon = ""
        )

        coEvery { localDataSource.addWeatherData(weatherEntity) } just Runs

        weatherRepository.cacheWeather(weatherEntity)

        coVerify { localDataSource.addWeatherData(weatherEntity) }
    }

}
