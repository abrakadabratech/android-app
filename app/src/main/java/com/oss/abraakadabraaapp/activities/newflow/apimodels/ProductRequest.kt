package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class ProductRequest(
    @SerializedName("name") val name:String = "",
    @SerializedName("description") val description:String = "",
    @SerializedName("condition") val condition:String = "",
    @SerializedName("used_for") val used_for:String = "",
    @SerializedName("location_name") val location_name:String = "",
    @SerializedName("category") val category:String = "",
    @SerializedName("latitude") val latitude:Double = 0.0,
    @SerializedName("longitude") val longitude:Double = 0.0,
    @SerializedName("product1") val product1:String = "",

)