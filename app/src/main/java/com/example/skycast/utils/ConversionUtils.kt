package com.example.skycast.utils

fun convertTemperature(temperature: Double, fromUnit: String, toUnit: String): Double {
    val normalizedFromUnit = fromUnit.lowercase()
    val normalizedToUnit = toUnit.lowercase()

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
        else -> temperature
    }
}

fun celsiusToFahrenheit(celsius: Double): Double = (celsius * 9 / 5) + 32
fun celsiusToKelvin(celsius: Double): Double = celsius + 273.15

fun fahrenheitToCelsius(fahrenheit: Double): Double = (fahrenheit - 32) * 5 / 9
fun fahrenheitToKelvin(fahrenheit: Double): Double = celsiusToKelvin(fahrenheitToCelsius(fahrenheit))

fun kelvinToCelsius(kelvin: Double): Double = kelvin - 273.15
fun kelvinToFahrenheit(kelvin: Double): Double = celsiusToFahrenheit(kelvinToCelsius(kelvin))



fun convertWindSpeed(speed: Double, fromUnit: String, toUnit: String): Double {
    if (fromUnit.lowercase() == toUnit.lowercase()) return speed

    return when (fromUnit.lowercase()) {
        "kmh" -> when (toUnit.lowercase()) {
            "mph" -> kmhToMph(speed)
            "knots" -> kmhToKnots(speed)
            "ms" -> kmhToMs(speed)
            else -> speed
        }

        "mph" -> when (toUnit.lowercase()) {
            "kmh" -> mphToKmh(speed)
            "knots" -> mphToKnots(speed)
            "ms" -> mphToMs(speed)
            else -> speed
        }

        "knots" -> when (toUnit.lowercase()) {
            "kmh" -> knotsToKmh(speed)
            "mph" -> knotsToMph(speed)
            "ms" -> knotsToMs(speed)
            else -> speed
        }

        "ms" -> when (toUnit.lowercase()) {
            "kmh" -> msToKmh(speed)
            "mph" -> msToMph(speed)
            "knots" -> msToKnots(speed)
            else -> speed
        }

        else -> speed
    }
}

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