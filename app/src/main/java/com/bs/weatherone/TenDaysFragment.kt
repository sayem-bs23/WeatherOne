package com.bs.weatherone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_ten_days.*

import kotlin.collections.ArrayList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import java.util.Date


class TenDaysFragment: Fragment(){
    lateinit var viewModel:MainViewModel

    //    val weatherContent = WeatherContent(this)
    var customAdapter = CustomAdapter(ArrayList<Weather>(), ::weatherItemClicked)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var temp = inflater.inflate(R.layout.fragment_ten_days, container, false)!!
        return temp
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weather_recycler_view.layoutManager = LinearLayoutManager(context)
        weather_recycler_view.adapter = customAdapter

        val lc = LocationController(context!!)
        lc.updateLocation()
        lc.locationListener = object : LocationController.LocationListener {
            override fun onNewLocation(lat: Double, lon: Double) {
                Log.d("dbg", "my lat $lat")
//                viewModel.setUserId(lat.toInt().toString())
                viewModel.setLatLon(lat.toInt().toString(), lon.toInt().toString())
            }
            override fun onLocationFailed() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }



        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.weatherData.observe(this, Observer{
            //            Log.d("api_resp", it.dayList[0].main?.temp.toString())
            Log.d("dbg_observer","Temp ${it[0].temperature}")
            Log.d("dbg", "abc")

            customAdapter.setData(it)

        })

        //viewModel.setUserId("1")




//        viewModel.setUserId(locCon.getLatitudeBasedOnSetting().toInt().toString())

//        val lc = LocationController(context!!)
//        Log.d("dbg","msg ${lc.getLongitudeBasedOnSetting()}")




        //getForecast()

    }


    private fun weatherItemClicked(weather: Weather, pos: Int) {
        val cat = listOf<String>("cat")
        Toast.makeText(context, "Clicked: ${weather.day}", Toast.LENGTH_LONG).show()
        Toast.makeText(context, "Clicked: ${pos}", Toast.LENGTH_LONG).show()
        val gson = Gson()
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("position", pos)
        intent.putExtra("obj", gson.toJson(weather))


        startActivity(intent)
    }


    fun getDay(idx: Int): String {
        val d = Date()
        return monthMapper[d.month].toString()
    }
}