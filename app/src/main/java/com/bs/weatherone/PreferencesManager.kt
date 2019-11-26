package com.bs.weatherone

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson

/**
 * Created by partha on 2/15/18.
 */
class PreferencesManager private constructor(private val _context: Context) {
    private val TAG = PreferencesManager::class.java.name
    private val mPref: SharedPreferences

    init {
        mPref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }



    companion object {
        private val PREF_NAME = "com.bs.weatherone"
        private val KEY_VALUE = "com.bs.weatherone.keyvalue"

        private var sInstance: PreferencesManager? = null


        @Synchronized
        fun initializeInstance(context: Context) {
            if (sInstance == null) {
                sInstance = PreferencesManager(context)
            }
        }

        val instance: PreferencesManager
            @Synchronized get() {
                if (sInstance == null) {
                    throw IllegalStateException(PreferencesManager::class.java.simpleName + " is not initialized, call initializeInstance(..) method first.") as Throwable
                }
                return sInstance as PreferencesManager
            }




        private const val keyCustomPlace = "nkey_custom_place"
        private const val keyCustomPlaceStatus = "nkey_custom_place_status"
        private const val keyLat = "nkey_latitude"
        private const val keyLon = "nkey_longitude"



        private const val keyLocationName = "nkey_key_location_name"

        private val keyEnsureFlag = "ensure_flag"

        fun getComplicationKey(complicationId: Int): String
        {
            return "complicationId${complicationId}"
        }

        private fun getPreference(appContext: Context): SharedPreferences {
            return appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }



        fun saveCustomPlaceStatus(appContext: Context, status: Boolean) {
            getPreference(appContext).edit {
                putBoolean(keyCustomPlaceStatus, status)
            }
        }

        fun saveCustomPlace(appContext: Context, placePrediction: PlacePrediction) {
            getPreference(appContext).edit {
                putString(keyCustomPlace, Gson().toJson(placePrediction))
            }
        }

        fun getCustomPlace(appContext: Context): PlacePrediction {
            val placeString = getPreference(appContext).getString(keyCustomPlace, Gson().toJson(PlacePrediction()))
            return Gson().fromJson(placeString, PlacePrediction::class.java)
        }


        fun getCustomPlaceStatus(appContext: Context): Boolean {
            return getPreference(appContext).getBoolean(keyCustomPlaceStatus, false)
        }


        fun saveLatitude(appContext: Context, lat: Float) {
            getPreference(appContext).edit {
                putFloat(keyLat, lat)
            }
        }

        fun getLatitude(appContext: Context): Float {
            return getPreference(appContext)
                    .getFloat(keyLat, 0.0f)
        }

        fun saveLongitude(appContext: Context, lat: Float) {
            getPreference(appContext).edit {
                putFloat(keyLon, lat)
            }
        }

        fun getLongitude(appContext: Context): Float {
            return getPreference(appContext)
                    .getFloat(keyLon, 0.0f)
        }


//        //Weather Response
//        fun saveCurrentWeather(appContext: Context, currentWeather: CurrentWeather) {
//            getPreference(appContext).edit {
//                putString(keyWeatherDataCurrent, Gson().toJson(currentWeather))
//            }
//        }
//
//        fun getCurrentWeather(appContext: Context): CurrentWeather? {
//            val weatherString = getPreference(appContext)
//                    .getString(keyWeatherDataCurrent, "")
//            return if (weatherString != "") Gson().fromJson(weatherString, CurrentWeather::class.java) else null
//        }
//


        //Location Name
        fun saveLocationName(appContext: Context, location: String) {
            getPreference(appContext).edit {
                putString(keyLocationName, location)
            }
        }

        fun getLocationName(appContext: Context): String {
            return getPreference(appContext)
                    .getString(keyLocationName, "") ?: ""
        }



    }

}