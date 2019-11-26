package com.bs.weatherone

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import com.google.android.gms.location.*

class LocationProvider private constructor(val context: Context) {
    private val TAG = this.javaClass.simpleName

    companion object : SingletonHolder<LocationProvider, Context>(::LocationProvider)

    var location: Location? = null

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            interval = 10 * 60 * 1000
            fastestInterval = 5000
            smallestDisplacement = 100f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    var onNewLocationListener: OnNewLocationListener? = null

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    init {
        if (isLocationPermissionGranted())
            initLocation()
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ((context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private val locationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            Log.i(TAG, "On Location Result Called locationResult: ${locationResult.toString()}")
            locationResult?.let { result ->
                location = result.lastLocation
                Log.i(TAG, "Location Changed - Lat: " + result.lastLocation.latitude + ", Lng: "
                        + result.lastLocation.longitude)
                onNewLocationListener?.onLocationUpdated(location!!.latitude, location!!.longitude)
            } ?: onNewLocationListener?.onLocationRetrievalFailed()


            stopLocationUpdates()
        }


    }



    @SuppressLint("MissingPermission")
    private fun initLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    this.location = it
                    onNewLocationListener?.onLocationUpdated(it.latitude, it.longitude)
                    Log.i(TAG, "Got last known location - Lat: " + it.latitude + ", Lng: " + it.longitude)
                } ?: requestLocationUpdate()
            }.addOnFailureListener {
                it.printStackTrace()
                onNewLocationListener?.onLocationRetrievalFailed()
            }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdate() {
        Log.i(TAG, "Requesting Location updateWeatherData")
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            null /*Looper*/
        )
    }

    private fun stopLocationUpdates() {
        Log.i(TAG, "Stopping Location updateWeatherData")
        fusedLocationClient.removeLocationUpdates(locationCallBack)
    }

    interface OnNewLocationListener {
        fun onLocationUpdated(lat: Double, lon: Double)
        fun onLocationRetrievalFailed()
    }
}