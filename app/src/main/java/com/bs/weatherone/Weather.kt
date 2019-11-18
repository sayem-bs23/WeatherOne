package com.bs.weatherone



data class Weather(val date:String, val day: String, val temperature:String, val foreCast: ForeCast){
    fun getForecastImageSource(): Int{
        when(foreCast){
            ForeCast.Clear -> return R.mipmap.sunny
            ForeCast.Clouds -> return R.mipmap.partly_cloudy
            ForeCast.Rain-> return R.mipmap.rain_heavy
        }

        return R.drawable.ic_launcher_foreground
    }


}


enum class ForeCast(val forecastValue: String) {
    Clear("Clear"),
    Clouds("Clouds"),
    Rain("Rain"),
    Unknown("Unknown")
}



val monthMapper = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

fun validateForecast(str:String):String{
    if(str != "Clear" && str != "Clouds" && str != "Rain"){
        return "Unknown"
    }
    return str
}