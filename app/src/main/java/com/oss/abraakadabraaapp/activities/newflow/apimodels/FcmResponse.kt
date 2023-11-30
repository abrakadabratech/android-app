package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class FcmResponse(
    @SerializedName("code") val code:Int,
    @SerializedName("status") val status:Int,
    @SerializedName("message") val message:String,
)