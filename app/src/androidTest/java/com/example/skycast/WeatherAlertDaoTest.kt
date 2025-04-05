package com.example.skycast

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.data.local.FavoriteDatabase
import com.example.skycast.data.local.alert.WeatherAlert
import com.example.skycast.data.local.alert.WeatherAlertDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherAlertDaoTest {

    private lateinit var db: FavoriteDatabase
    private lateinit var weatherAlertDao: WeatherAlertDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            FavoriteDatabase::class.java
        ).build()

        weatherAlertDao = db.weatherAlertDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertWeatherAlert_addsAlertToDatabase() = runBlocking {
        val weatherAlert = WeatherAlert(id = 1, alertMessage = "Storm Alert", time = System.currentTimeMillis(), isActive = true)

        weatherAlertDao.insertWeatherAlert(weatherAlert)

        val activeAlerts = weatherAlertDao.getActiveWeatherAlerts().first()
        assertEquals(1, activeAlerts.size)
        assertEquals("Storm Alert", activeAlerts[0].alertMessage)
    }

    @Test
    fun deleteWeatherAlert_removesAlertFromDatabase() = runBlocking {
        val weatherAlert = WeatherAlert(id = 1, alertMessage = "Storm Alert", time = System.currentTimeMillis(), isActive = true)

        weatherAlertDao.insertWeatherAlert(weatherAlert)

        weatherAlertDao.deleteWeatherAlert(weatherAlert)

        val activeAlerts = weatherAlertDao.getActiveWeatherAlerts().first()
        assertEquals(0, activeAlerts.size)
    }
}
