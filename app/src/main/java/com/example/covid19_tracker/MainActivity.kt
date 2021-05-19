package com.example.covid19_tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.pulkit.covidindiatracker.SpannableDelta
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var stateAdapter: StateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.addHeaderView(LayoutInflater.from(this).inflate(R.layout.item_header,list,false))
        fetchResults()
    }

    private fun fetchResults() {
        GlobalScope.launch {// Global Scope of Courountines gets on
            val response = withContext(Dispatchers.IO) { Client.api.execute() }   // Call API using coroutines because of network calls | Dispatcher IO means Input/Output call
            if (response.isSuccessful) {  // response succesfull
                val data = Gson().fromJson(response.body?.string(), Response::class.java) // Conversion from API data to android understanble using Gson
                launch(Dispatchers.Main) {  // UI work
                    bindCombinedData(data.statewise[0]) // Access list data
                    bindStateWiseData(data.statewise.subList(1, data.statewise.size))
                }
            }
        }
    }

    private fun bindStateWiseData(subList: List<StatewiseItem>) {
           stateAdapter = StateAdapter(subList)
           list.adapter = stateAdapter
    }

    private fun bindCombinedData(data: StatewiseItem){
        val lastUpdatedTime = data.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        lastUpdatedTv.text = "Last Updated\n ${getTimeAgo(
            simpleDateFormat.parse(lastUpdatedTime)  
        )}"

        confirmedTv.text = data.confirmed
        activeTv.text = data.active
        recoveredTv.text = data.recovered
        deceasedTv.text = data.deaths

    }
}

fun getTimeAgo(past: Date): String {
    // Show updated at Time
    val now = Date()  // Current Time 
    val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
    val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

    return when {
        seconds < 60 -> {
            "Few seconds ago"
        }
        minutes < 60 -> {
            "$minutes minutes ago"
        }
        hours < 24 -> {
            "$hours hour ${minutes % 60} min ago"
        }
        else -> {
            SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
        }
    }
}
