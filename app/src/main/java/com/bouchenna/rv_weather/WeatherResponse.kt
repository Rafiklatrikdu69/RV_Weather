package com.bouchenna.rv_weather

class WeatherResponse {
    data class Coord(
        val lon: Double,
        val lat: Double
    )

    data class Weather(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
    )

    data class Main(
        val temp: Double,
        val feels_like: Double,
        val temp_min: Double,
        val temp_max: Double,
        val pressure: Int,
        val humidity: Int
    )

    data class Wind(
        val speed: Double,
        val deg: Int,
        val gust: Double
    )

    data class Clouds(
        val all: Int
    )

    data class Sys(
        val type: Int,
        val id: Int,
        val country: String,
        val sunrise: Long,
        val sunset: Long
    )

    val coord: Coord? = null
    val weather: List<Weather>? = null
    val base: String? = null
    val main: Main? = null
    val visibility: Int? = null
    val wind: Wind? = null
    val clouds: Clouds? = null
    val dt: Long? = null
    val sys: Sys? = null
    val timezone: Int? = null
    val id: Int? = null
    val name: String? = null
    val cod: Int? = null
}
