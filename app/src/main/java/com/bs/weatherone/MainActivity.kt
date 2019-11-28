package com.bs.weatherone

import android.Manifest
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.diaglog_custom_location.*
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.view.Gravity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.bs.weatherone.LatLon
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var autoSuggestAdapter:AutoSuggestAdapter? = null
    val tenDaysFragment = TenDaysFragment()
    val todayFragment = TodayFragment()

    val locationSuggested = arrayOf(
        "Bangladesh", "India", "Pakistan", "China"
    )

    val locationMapToLatLon = mapOf("Bangladesh" to LatLon("23", "90"),
        "India" to LatLon("20", "78"),
        "Pakistan" to LatLon("30", "69"),
        "China" to LatLon("35", "104")
    )

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.location_custom)
//        val searchView = searchItem?.actionView as SearchView
//
////        searchView.suggestionsAdapter = CursorAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayListOf("a","b"))
//        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                searchView.clearFocus()
//                searchView.setQuery("", false)
//                searchItem.collapseActionView()
//                Toast.makeText(this@MainActivity, "searched $query", Toast.LENGTH_SHORT).show()
//
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//
//
//        })
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        autoSuggestAdapter = AutoSuggestAdapter(this,android.R.layout.simple_dropdown_item_1line)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(todayFragment, "Today")
        adapter.addFragment(tenDaysFragment, "Ten days")


        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

//        val longitude = lc.getLongitudeBasedOnSetting()
//
//        Log.d("dbg","my longitude $longitude")

        fromDevice()


    }

    private fun fromDevice() {
        if(checkPermissions()) {
            if (isLocationEnabled()) {
                val lc = LocationController(this)
                lc.updateLocation()
                lc.locationListener = object : LocationController.LocationListener {
                    override fun onNewLocation(lat: Double, lon: Double) {
                        Log.d("dbg", "my lat $lat")
                        tenDaysFragment.viewModel.setLatLon(lat.toString(), lon.toString())
                        todayFragment.updateData(lat.toString(), lon.toString())
                    }
                    override fun onLocationFailed() {
                        //do failed job
                    }

                }
            }

            else{
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                fromDevice()
            }
        }
        else{
            requestPermissions()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
//           R.id.location_from_device -> Toast.makeText(this, "Take from location", Toast.LENGTH_SHORT).show()
           R.id.location_custom -> {
               customLocationSearch()
//               showCustomLocationDialog()

           }
           R.id.location_from_device ->{
               fromDevice()

           }

       }

        return true
    }

    fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            42
        )
    }
    private fun customLocationSearch() {

        supportActionBar?.setDisplayShowTitleEnabled(false)
        val dialog = Dialog(
            this,
            android.R.style.Theme_Translucent_NoTitleBar
        )
        dialog.setCanceledOnTouchOutside(true)
        val view:View = layoutInflater.inflate(R.layout.diaglog_custom_location, null)

        // Setting dialogview
        val window = dialog.getWindow()
        window?.setGravity(Gravity.CENTER)

        window?.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT)
        dialog.setTitle(null)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        val locationEditText = view.findViewById(R.id.locationEditText) as EditText
        locationSuggestionHandler(view)

        val searchBtn = view.findViewById<Button>(R.id.btn_search)
        searchBtn.setOnClickListener {
            Toast.makeText(this, "ok search", Toast.LENGTH_SHORT).show()

            val apiKey = "AIzaSyCTlaLxwG91VdqfSR6YYA-BfXxcftO-btI"
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, apiKey )
            }

            val queryText = locationEditText.text.toString()
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(queryText)
                .build()


            val placesClient = Places.createClient(this)
            val alist = ArrayList<String>()
            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                val mResult = StringBuilder()

                for (prediction in response.autocompletePredictions) {
                    var s = prediction.getPrimaryText(null).toString()
                    alist.add(s)
                }

                autoSuggestAdapter?.setData(alist)
                autoSuggestAdapter?.notifyDataSetChanged()
                Log.d("dbg","size_arr_list $alist.size")
                Toast.makeText(this, mResult, Toast.LENGTH_LONG).show()

                searchBtn.setText("set location")
                searchBtn.setOnClickListener {

                    /***/
                    val editTextAfterSelection: EditText = view.findViewById(R.id.locationEditText) as EditText
                    val check = editTextAfterSelection.text.toString()
                    Log.d("dbg","check $check")

                    var placeIdFromPrimaryText= ""
                    var placeNameFromPrimaryText= ""
                    if(check != null){
                        for (prediction in response.autocompletePredictions) {
                            var s = prediction.getPrimaryText(null).toString()

                            if (s == check) {
                                Toast.makeText(
                                    this,
                                    "name: ${prediction.getPrimaryText(null)} key ${prediction.placeId}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                placeNameFromPrimaryText = prediction.getPrimaryText(null).toString()
                                placeIdFromPrimaryText = prediction.placeId
                                break
                            }
                        }

                    }


                    val placeId = placeIdFromPrimaryText

                    // Specify the fields to return.
                    val placeFields :List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                    Log.d("dbg","request place- name:${placeNameFromPrimaryText} id:${placeId}")
                    val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields)
                    placesClient.fetchPlace(request).addOnSuccessListener {

                        val place: Place = it.getPlace()
                        Log.d("dbg", "response place- name:${place.name} id: ${place.id}")
                        //                        Log.i(TAG, "Place found: " + place.getName());
                        Log.d("dbg", "reverse look up: ${place.latLng?.latitude.toString()}")
                        if(place.latLng != null){
                            val latlon: LatLon? = LatLon(
                                place.latLng?.latitude?.toInt().toString(),
                                place.latLng?.longitude?.toInt().toString()
                            )
                            tenDaysFragment.viewModel.setLatLon(latlon!!.lat, latlon!!.lon)
                            todayFragment.updateData(latlon!!.lat, latlon!!.lon)
                        }

                    }.addOnFailureListener{
                        Log.d("dbg","place not found exception ${it.printStackTrace()}")
                    }

                    dialog.dismiss()
                    supportActionBar?.setDisplayShowTitleEnabled(true)
                }
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
    //                    Log.e(FragmentActivity.TAG, "Place not found: " + exception.statusCode)
                }
            }

            /***/
        }



        dialog.show()




//        supportActionBar.setDisplayShowTitleEnabled(false)
    }

    private fun showCustomLocationDialog() {
        val context = this
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Custom locaiton")
        val view = layoutInflater.inflate(R.layout.diaglog_custom_location, null)
        val locationEditText = view.findViewById(R.id.locationEditText) as EditText

        builder.setView(view)
        locationSuggestionHandler(view)

        val searchBtn = view.findViewById<Button>(R.id.btn_search)
        searchBtn.setOnClickListener({
            Toast.makeText(this, "ok search", Toast.LENGTH_SHORT).show()

            val apiKey = "AIzaSyCTlaLxwG91VdqfSR6YYA-BfXxcftO-btI"
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, apiKey )
            }

            val queryText = locationEditText.text.toString()
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(queryText)
                .build()


            val placesClient = Places.createClient(this)
            val alist = ArrayList<String>()
            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                val mResult = StringBuilder()
                response.autocompletePredictions[0].placeId
                for (prediction in response.autocompletePredictions) {
                    var s = prediction.getPrimaryText(null).toString()
                    alist.add(s)
                }

                autoSuggestAdapter?.setData(alist)
                autoSuggestAdapter?.notifyDataSetChanged()
                Log.d("dbg","size_arr_list $alist.size")
                Toast.makeText(this, mResult, Toast.LENGTH_LONG).show()
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
//                    Log.e(FragmentActivity.TAG, "Place not found: " + exception.statusCode)
                }
            }

        })





        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            val locationInput:String = locationEditText.text.toString()//input
            var isValid = true
            if (locationInput.isBlank()) {
                locationEditText.error = "cannot be empty"
                isValid = false
            }

            if (isValid) {

                Toast.makeText(context, "location set to $locationInput", Toast.LENGTH_LONG).show()
            }

            if (isValid) {
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show();
    }


    fun locationSuggestionHandler(view:View){

        var autoCompleteTextView: AutoCompleteTextView  = view.findViewById(R.id.locationEditText)
        autoCompleteTextView.setAdapter(autoSuggestAdapter)
        autoCompleteTextView.threshold = 1
        autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            Toast.makeText(this,"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }
        autoCompleteTextView.setOnDismissListener {
            Toast.makeText(this,"Suggestion closed.",Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.root_layout).setOnClickListener{
            val text = autoCompleteTextView.text
            Toast.makeText(this,"Inputted : $text",Toast.LENGTH_SHORT).show()
        }

        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener{
                view, b ->
            if(b){
                // Display the suggestion dropdown on focus
                autoCompleteTextView.showDropDown()
            }
        }
    }



}


class AutoSuggestAdapter(context:Context, resource: Int):ArrayAdapter<String>(context , resource), Filterable{
    private val mListData = ArrayList<String>()
    fun setData(list:ArrayList<String>){
        mListData.clear()
        mListData.addAll(list)
    }

    override fun getCount(): Int {
        return mListData.size
    }

    override fun getItem(position: Int): String? {
        return mListData.get(position)
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    filterResults.values = mListData
                    filterResults.count = mListData.size
                }
                return filterResults;
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }


}











