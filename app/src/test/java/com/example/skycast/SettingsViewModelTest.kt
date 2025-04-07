package com.example.skycast

import android.app.Application
import com.example.skycast.data.repo.WeatherRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.rules.TestRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.skycast.viewModel.SettingsViewModel
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val instantExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var application: Application
    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mockk()
        repository = mockk()

        every { repository.getLocationMode() } returns "GPS"
        every { repository.getTemperatureUnit() } returns "Fahrenheit"
        every { repository.getWindSpeedUnit() } returns "miles/hour"
        every { repository.getLanguage() } returns "English"

        viewModel = SettingsViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setTemperatureUnit updates windSpeedUnit based on Celsius`() = runTest {
        coEvery { repository.setTemperatureUnit(any()) } just Runs
        coEvery { repository.setWindSpeedUnit(any()) } just Runs

        viewModel.setTemperatureUnit("Celsius")
        advanceUntilIdle()

        assertThat(viewModel.temperatureUnit.value, `is`("Celsius"))
        assertThat(viewModel.windSpeedUnit.value, `is`("meter/sec"))
        coVerify { repository.setWindSpeedUnit("meter/sec") }
    }


    @Test
    fun `setWindSpeedUnit updates windSpeedUnit in repository`() = runTest {
        coEvery { repository.setWindSpeedUnit(any()) } just Runs

        viewModel.setWindSpeedUnit("km/h")
        advanceUntilIdle()

        assertThat(viewModel.windSpeedUnit.value, `is`("km/h"))
        coVerify { repository.setWindSpeedUnit("km/h") }
    }
}
