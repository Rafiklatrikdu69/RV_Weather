package com.bouchenna.rv_weather.service

import com.bouchenna.rv_weather.AirPollutionResponse
import com.bouchenna.rv_weather.WeatherForecastResponse
import com.bouchenna.rv_weather.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
    @GET("air_pollution/forecast")
    fun getAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("appid") apiKey: String
    ): Call<AirPollutionResponse>

    @GET("forecast")
    fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherForecastResponse>
}
