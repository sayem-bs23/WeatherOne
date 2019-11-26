package com.bs.weatherone


import androidx.lifecycle.*

class MainViewModel: ViewModel(){

    private val _latlon: MutableLiveData<LatLon> = MutableLiveData()

    val weatherData: LiveData<ArrayList<Weather>> = Transformations.switchMap(_latlon){ latlon:LatLon ->
        Repository.getCurrentLiveData(latlon.lat, latlon.lon)
    }

    fun cancelJob(){
        Repository.cancelJobs()
    }

    //act as trigger
    fun setLatLon(lat:String, lon:String){
        val updateLat = lat
        val updateLon = lon

        if(_latlon.value?.lat == lat && _latlon.value?.lon == lon){
            return
        }
        _latlon.value = LatLon(lat, lon)

    }
}







