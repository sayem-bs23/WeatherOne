package com.bs.weatherone


import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*


class LocationController(val context: Context) {
    private val TAG = this.javaClass.simpleName

    interface LocationListener {
        fun onNewLocation(lat: Double, lon: Double)
        fun onLocationFailed()
    }

    companion object : SingletonHolder<LocationController, Context>(::LocationController) {
        const val defaultCustomPlaceLat = 0.0
        const val defaultCustomPlaceLon = 0.0
    }

    var locationListener: LocationListener? = null
    //isCustomLocationOn tracks whether the custom location from setting is off or on
    private var isCustomLocationOn = false

    private val locationProvider = LocationProvider.getInstance(context).apply {
        onNewLocationListener = object : LocationProvider.OnNewLocationListener {
            override fun onLocationUpdated(lat: Double, lon: Double) {
                Log.i(TAG, "New location received")
                Log.i(TAG, "lat : ${lat.toFloat()}")
                Log.i(TAG, "lon : ${lon.toFloat()}")
                PreferencesManager.saveLatitude(this@LocationController.context, lat.toFloat())
                PreferencesManager.saveLongitude(this@LocationController.context, lon.toFloat())

                locationListener?.onNewLocation(lat, lon)
            }

            override fun onLocationRetrievalFailed() {
                locationListener?.onLocationFailed()
            }
        }
    }

    /**
     * this method checks the settings and update
     * local variable (isCustomLocationOn) based on settings
     */
    private fun checkLocationStatus() {
//        isCustomLocationOn = PreferencesManager.getCustomPlaceStatus(context)
    }


    /**
     * provides the longitude based on watchface settings
     * either form custom location (if on by weatherData)
     * from actual location
     */
    fun getLatitudeBasedOnSetting(): Double {
        checkLocationStatus()
        return if (isCustomLocationOn)
            PreferencesManager.getCustomPlace(context).lat ?: defaultCustomPlaceLat
        else
            PreferencesManager.getLatitude(context).toDouble()
    }

    /**
     * provides the latitude based on watchface settings
     * either form custom location (if on by weatherData)
     * from actual location
     */
    fun getLongitudeBasedOnSetting(): Double {
        checkLocationStatus()
        return if (isCustomLocationOn)
            PreferencesManager.getCustomPlace(context).lon ?: defaultCustomPlaceLon
        else
            PreferencesManager.getLongitude(context).toDouble()
    }


    /**
     * updates lat lon using watch gps
     * and calls onNewLocationListener if successful
     */
    fun updateLocation() {
        checkLocationStatus()
        if (isCustomLocationOn) {
            Log.i(TAG, "Returning stored location coordinates")

            locationListener?.onNewLocation(getLatitudeBasedOnSetting(), getLongitudeBasedOnSetting())
        } else {
            Log.i(TAG, "Requesting new location")
            locationProvider.requestLocationUpdate()
        }
    }


}