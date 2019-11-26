package com.bs.weatherone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.diaglog_custom_location.*


class MainActivity : AppCompatActivity() {
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
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
               showCustomLocationDialog()
//               sid()
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


    private fun showCustomLocationDialog() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Custom locaiton")
        val view = layoutInflater.inflate(R.layout.diaglog_custom_location, null)
        val locationEditText = view.findViewById(R.id.locationEditText) as EditText
        locationSuggestionHandler(view) //why sendin view? need to find the internal id of view
        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            val locationInput:String = locationEditText.text.toString()//input
            var isValid = true
            if (locationInput.isBlank()) {
                locationEditText.error = "cannot be empty"
                isValid = false
            }

            if (isValid) {
                // do something
                val latlon: LatLon? = locationMapToLatLon.get(locationInput)
                tenDaysFragment.viewModel.setLatLon(latlon!!.lat, latlon!!.lon)
                todayFragment.updateData(latlon!!.lat, latlon!!.lon)
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
        // Initialize a new array with elements


        // Initialize a new array adapter object
        val adapter = ArrayAdapter<String>(
            this, // Context
            android.R.layout.simple_dropdown_item_1line, // Layout
            locationSuggested // Array
        )


        // Set the AutoCompleteTextView adapter
        autoCompleteTextView.setAdapter(adapter)


        // Auto complete threshold
        // The minimum number of characters to type to show the drop down
        autoCompleteTextView.threshold = 1


        // Set an item click listener for auto complete text view
        autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Display the clicked item using toast
            Toast.makeText(this,"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }


        // Set a dismiss listener for auto complete text view
        autoCompleteTextView.setOnDismissListener {
            Toast.makeText(this,"Suggestion closed.",Toast.LENGTH_SHORT).show()
        }


        // Set a click listener for root layout
        view.findViewById<LinearLayout>(R.id.root_layout).setOnClickListener{
            val text = autoCompleteTextView.text
            Toast.makeText(this,"Inputted : $text",Toast.LENGTH_SHORT).show()
        }


        // Set a focus change listener for auto complete text view
        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener{
                view, b ->
            if(b){
                // Display the suggestion dropdown on focus
                autoCompleteTextView.showDropDown()
            }
        }
    }



}

