package com.bs.weatherone

import kotlinx.coroutines.*
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


object Repository{

    val AppId= "bb4dd20e432ae754a84c5b91ee475854"
    val lat = "23"
    val lon = "90"
    val units = "metric"
    var job:CompletableJob? = null //more control on asynchronous job

    fun getUser(userId: String): LiveData<ArrayList<Weather>>{


        val isOnline = true
        if(isOnline) {
            return getUserFromNetwork()
        }
        else{
            return getUserFromLocal()
        }
    }

    private fun getUserFromLocal(): LiveData<ArrayList<Weather>> {
        return object : LiveData<ArrayList<Weather>>() {
            override fun onActive() {
                super.onActive()
                value = dummyData()
            }
        }
    }

    private fun dummyData(): ArrayList<Weather> {
        val weatherList = ArrayList<Weather>()

        weatherList.add(Weather("January 7", "sunday", "23", ForeCast.Clear))
        weatherList.add(Weather("January 8", "monday", "21", ForeCast.Rain))
        weatherList.add(Weather("January 9", "tuesday", "29", ForeCast.Clouds))
        weatherList.add(Weather("January 10", "wednesday", "22", ForeCast.Clear))
        weatherList.add(Weather("January 11", "thursday", "25", ForeCast.Rain))

        return weatherList
    }

    private fun getUserFromNetwork():LiveData<ArrayList<Weather>> {
        job = Job()
        return object : LiveData<ArrayList<Weather>>(){
            override fun onActive() {
                super.onActive() //when the method is called, I want to get the value

                job?.let{theJob->
                    CoroutineScope(IO + theJob).launch{
                        val forecastResponse = RetrofitSingleton
                            .forecastInstance
                            .getTenDaysForecastData(lat, lon, AppId, units)

                        var weatherList = ArrayList<Weather>()


                        val dayMapper = listOf<String>("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday","Saturday")
                        forecastResponse.dayList.forEachIndexed { idx, weatherResponse ->
                            if(idx > 7){
                                return@forEachIndexed
                            }

                            val date = monthMapper[Date().month].toString() + " "+ getDate(idx)
                            val day = dayMapper[(Date().day + idx)%7].toString()
                            val temp = weatherResponse.main!!.temp.roundToInt().toString()
                            val forecast = validateForecast(weatherResponse.WeatherAPI[0]!!.main.toString() )

                            weatherList.add( Weather(date,
                                day,
                                temp,
                                ForeCast.valueOf(forecast))
                            )

//                        weatherList_dataSource = weatherList

                        }


                        withContext(Main){
                            value = weatherList //setValue in live data

                            theJob.complete()
                        }
                    }
                }
            }
        }
    }

    fun cancelJobs(){
        job?.cancel() //later in the view model, when the activity is destroyed, we want to cancel

    }

}



fun getDate(idx:Int):String{
    val d = Date()

    return  (d.date+idx).toString()
}

