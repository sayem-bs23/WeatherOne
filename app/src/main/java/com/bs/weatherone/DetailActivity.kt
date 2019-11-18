package com.bs.weatherone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.card_details.*
import java.util.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_details)

//
        val position = intent.extras?.get("position").toString().toInt()
        val gson = Gson()
        val strObj = intent.getStringExtra("obj")
        val weather = gson.fromJson<Weather>(strObj, Weather::class.java!!)


        temperature_cardDetail.text = weather.temperature
        weatherSatus_cardDetail.text = weather.foreCast.forecastValue
        weatherAvatar_cardDetail.setImageResource(weather.getForecastImageSource())
        val d = Date()
        dateAndTimeTop_cardDetail.text = "${d.date.toString()} ${monthMapper[d.month].toString()},"


    }
}