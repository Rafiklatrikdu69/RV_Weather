package com.bouchenna.rv_weather.service

import com.bouchenna.rv_weather.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET

interface Api_Weather {
    @GET("")
    fun index(): Call<List<WeatherResponse>>


}
