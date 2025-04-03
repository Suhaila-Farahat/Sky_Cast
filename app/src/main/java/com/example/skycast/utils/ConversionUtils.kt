package com.example.skycast.utils

// Function to convert temperature between units
fun convertTemperature(temperature: Double, fromUnit: String, toUnit: String): Double {
    val normalizedFromUnit = fromUnit.lowercase()
    val normalizedToUnit = toUnit.lowercase()

    // If the units are the same, return the original temperature
    if (normalizedFromUnit == normalizedToUnit) return temperature

    return when (normalizedFromUnit) {
        "celsius" -> when (normalizedToUnit) {
            "fahrenheit" -> celsiusToFahrenheit(temperature)
            "kelvin" -> celsiusToKelvin(temperature)
            else -> temperature
        }
        "fahrenheit" -> when (normalizedToUnit) {
            "celsius" -> fahrenheitToCelsius(temperature)
            "kelvin" -> fahrenheitToKelvin(temperature)
            else -> temperature
        }
        "kelvin" -> when (normalizedToUnit) {
            "celsius" -> kelvinToCelsius(temperature)
            "fahrenheit" -> kelvinToFahrenheit(temperature)
            else -> temperature
        }
        else -> temperature // Return original temperature if unsupported unit
    }
}

// Helper functions for temperature conversion
fun celsiusToFahrenheit(celsius: Double): Double = (celsius * 9 / 5) + 32
fun celsiusToKelvin(celsius: Double): Double = celsius + 273.15

fun fahrenheitToCelsius(fahrenheit: Double): Double = (fahrenheit - 32) * 5 / 9
fun fahrenheitToKelvin(fahrenheit: Double): Double = celsiusToKelvin(fahrenheitToCelsius(fahrenheit))

fun kelvinToCelsius(kelvin: Double): Double = kelvin - 273.15
fun kelvinToFahrenheit(kelvin: Double): Double = celsiusToFahrenheit(kelvinToCelsius(kelvin))


// Function to convert wind speed between units
fun convertWindSpeed(speed: Double, fromUnit: String, toUnit: String): Double {
    val normalizedFromUnit = fromUnit.lowercase()
    val normalizedToUnit = toUnit.lowercase()

    // If the units are the same, return the original speed
    if (normalizedFromUnit == normalizedToUnit) return speed

    return when (normalizedFromUnit) {
        "kmh" -> when (normalizedToUnit) {
            "mph" -> kmhToMph(speed)
            "knots" -> kmhToKnots(speed)
            "ms" -> kmhToMs(speed)
            else -> speed
        }
        "mph" -> when (normalizedToUnit) {
            "kmh" -> mphToKmh(speed)
            "knots" -> mphToKnots(speed)
            "ms" -> mphToMs(speed)
            else -> speed
        }
        "knots" -> when (normalizedToUnit) {
            "kmh" -> knotsToKmh(speed)
            "mph" -> knotsToMph(speed)
            "ms" -> knotsToMs(speed)
            else -> speed
        }
        "ms" -> when (normalizedToUnit) {
            "kmh" -> msToKmh(speed)
            "mph" -> msToMph(speed)
            "knots" -> msToKnots(speed)
            else -> speed
        }
        else -> speed
    }
}

// Helper functions for wind speed conversion
fun kmhToMph(kmh: Double): Double = kmh * 0.621371
fun kmhToKnots(kmh: Double): Double = kmh * 0.539957
fun kmhToMs(kmh: Double): Double = kmh / 3.6

fun mphToKmh(mph: Double): Double = mph * 1.60934
fun mphToKnots(mph: Double): Double = mph * 0.868976
fun mphToMs(mph: Double): Double = mph * 0.44704

fun knotsToKmh(knots: Double): Double = knots * 1.852
fun knotsToMph(knots: Double): Double = knots * 1.15078
fun knotsToMs(knots: Double): Double = knots * 0.514444

fun msToKmh(ms: Double): Double = ms * 3.6
fun msToMph(ms: Double): Double = ms * 2.23694
fun msToKnots(ms: Double): Double = ms * 1.94384
