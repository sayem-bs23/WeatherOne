package com.bs.weatherone


import com.google.gson.annotations.SerializedName

class ForecastResponse {

    @SerializedName("list")
    var dayList = ArrayList<WeatherResponse>()

}

