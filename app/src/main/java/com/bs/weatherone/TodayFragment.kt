package com.bs.weatherone


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_today.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt

class TodayFragment: Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_today, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val d = Date()
        dateAndTimeTop.text =  "${d.date.toString()} ${monthMapper[d.month].toString()},"
        temperature.text = "23" + "°"
        getCurrentData()

    }

    companion object{
        val AppId= "bb4dd20e432ae754a84c5b91ee475854"
        val lat = "23"
        val lon = "90"
        val units = "metric"
    }

    internal fun getCurrentData() {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        val retrofit = RetrofitSingleton.todayInstance
        val call = retrofit.getCurrentWeatherData(lat, lon, AppId, units)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {

                Log.d("resp", "dbg")
                if (response.code() == 200) {
                    val weatherResponse = response.body()!!

                    val date = Date().date.toString()
                    val day = monthMapper[Date().month].toString()
                    val temp = weatherResponse.main!!.temp.roundToInt().toString()
                    val forecast = validateForecast(weatherResponse.WeatherAPI[0]!!.main.toString() )

                    val obj =  Weather(date,
                        day,
                        temp,
                        ForeCast.valueOf(forecast))
                    //onSuccess(list0

                    temperature.text = obj.temperature
                    weatherSatus.text = obj.foreCast.forecastValue.toString()
                    weatherAvatar.setImageResource(obj.getForecastImageSource())

                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
            }
        })
    }


}
