package com.bs.weatherone

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitSingleton{
    private const val BASE_URL = "http://api.openweathermap.org/"

    val retrofitBuilder: Retrofit.Builder by lazy {

        //build logger client
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
    }

    val todayInstance: WeatherService by lazy{
        retrofitBuilder
            .build()
            .create(WeatherService::class.java)

    }

    val forecastInstance: ForecastService by lazy{
        retrofitBuilder
            .build()
            .create(ForecastService::class.java)

    }

}