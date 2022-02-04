package com.alfrosoft.fastchatapp.fragments

import com.alfrosoft.fastchatapp.notifications.MyResponse
import com.alfrosoft.fastchatapp.notifications.Sender

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface APIService {

    @Headers(*["Content-Type:application/json",
        "Authorization:key= "])

    @POST("fcm/send")
    fun sendNotification(@Body body: Sender?): Call<MyResponse?>?


}