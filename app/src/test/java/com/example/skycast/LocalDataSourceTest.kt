package com.example.skycast


import com.example.skycast.data.local.LocalDataSource
import com.example.skycast.data.local.alert.WeatherAlert
import com.example.skycast.data.local.alert.WeatherAlertDao
import com.example.skycast.data.local.fav.FavoriteLocationDao
import com.example.skycast.data.local.fav.FavoriteLocationEntity
import com.example.skycast.data.local.home.WeatherDao
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class LocalDataSourceTest {

    private lateinit var favoriteLocationDao: FavoriteLocationDao
    private lateinit var weatherAlertDao: WeatherAlertDao
    private lateinit var weatherDao: WeatherDao
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        favoriteLocationDao = mockk()
        weatherAlertDao = mockk()
        weatherDao = mockk()

        localDataSource = LocalDataSource(favoriteLocationDao, weatherAlertDao, weatherDao)
    }

    @After
    fun tearDown() {
        clearMocks(favoriteLocationDao, weatherAlertDao, weatherDao)
    }

    @Test
    fun `addFavoriteLocation calls dao insert method`() = runBlocking {
        val favoriteLocation = FavoriteLocationEntity(
            id = 1,
            name = "Cairo",
            latitude = 30.0444,
            longitude = 31.2357
        )

        coEvery { favoriteLocationDao.insertFavoriteLocation(favoriteLocation) } just Runs

        localDataSource.addFavoriteLocation(favoriteLocation)

        coVerify { favoriteLocationDao.insertFavoriteLocation(favoriteLocation) }
    }



    @Test
    fun `addWeatherAlert calls dao insert method`() = runBlocking {
        val weatherAlert = WeatherAlert(
            id = 1,
            alertMessage = "Storm Alert",
            time = System.currentTimeMillis(),
        )

        coEvery { weatherAlertDao.insertWeatherAlert(weatherAlert) } just Runs

        localDataSource.addWeatherAlert(weatherAlert)

        coVerify { weatherAlertDao.insertWeatherAlert(weatherAlert) }
    }
}
