package com.example.covid19_tracker

import okhttp3.OkHttpClient
import okhttp3.Request

object Client {
    private val okHttpClient = OkHttpClient()  //OkHTTP Client formed, this we use for Networking

    private val request = Request.Builder()  // Used for Requesting Information
        .url("https://api.covid19india.org/data.json")
        .build()

    val api = okHttpClient.newCall(request)  // This tells that we have to call "request" from okHttpClient
  }
