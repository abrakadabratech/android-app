package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class FcmRequest(
    @SerializedName("data") var data:FCMData = FCMData()
)

data class FCMData(
    @SerializedName("fcmToken") var fcmToken:String = ""
){
    constructor():this("")
}