package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class RequestUserUpdate(
    @SerializedName("data" ) var data : _Data? = _Data()

)
data class _Data (

    @SerializedName("name"     ) var name     : String?   = "",
    @SerializedName("email"    ) var email    : String?   = "",
    @SerializedName("location" ) var location : Location? = Location()

)
data class Location (

    @SerializedName("lat" ) var lat : String? = "",
    @SerializedName("lng" ) var lng : String? = ""

)