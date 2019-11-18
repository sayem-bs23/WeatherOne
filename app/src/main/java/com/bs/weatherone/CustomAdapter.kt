package com.bs.weatherone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CustomAdapter (val weatherList: ArrayList<Weather>,  val clickListener: (Weather, Int) -> Unit): RecyclerView.Adapter<CustomAdapter.ViewHolder>(){ //Extend the Adapter


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.card_weather, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather: Weather = weatherList[position]

        holder?.date.text = weather.date //from data source we are putting the date to the place holder
        holder?.day.text = weather.day
        holder?.temperature.text = weather.temperature + "°"
        holder?.foreCastImage.setImageResource(weather.getForecastImageSource())
        holder?.bind(weatherList[position], clickListener, position)

    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){ //ViewHolder & Click Listener
        val date = itemView.findViewById(R.id.date_cardTextView) as TextView
        val day = itemView.findViewById(R.id.day_cardTextView) as TextView
        val foreCastImage = itemView.findViewById(R.id.weatherCard_imageView) as ImageView
        val temperature = itemView.findViewById(R.id.temperature__cardTextView) as TextView

        fun bind(weather: Weather, clickListener: (Weather, Int) -> Unit, position: Int){

            itemView.setOnClickListener({clickListener(weather,position)})  //Assign Click Listener to the View Holder
        }

    }

    fun setData(wList: ArrayList<Weather>){
        weatherList.clear()
        weatherList.addAll((wList))
        notifyDataSetChanged()
    }
}