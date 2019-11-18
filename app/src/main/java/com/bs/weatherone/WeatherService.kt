package com.bs.weatherone


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather?")
    fun getCurrentWeatherData(@Query("lat") lat: String,
                              @Query("lon") lon: String,
                              @Query("APPID") app_id: String,
                              @Query("units") units: String
    ): Call<WeatherResponse>
}

