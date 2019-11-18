package com.bs.weatherone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(TodayFragment(), "Today")
        adapter.addFragment(TenDaysFragment(), "Ten days")

        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

//        tabs.setupWithViewPager(viewPager)


//        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        viewModel.user.observe(this, Observer{
////            Log.d("api_resp", it.dayList[0].main?.temp.toString())
//            Log.d("dbg_observer",it[0].day)
//            Log.d("dbg", "abc")
//
//            customAdapter.setData(it)
//            customAdapter.setData(it)
//
//
//        })

//        viewModel.setUserId("1")


    }


}

