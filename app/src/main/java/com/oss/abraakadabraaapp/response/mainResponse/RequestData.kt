package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class RequestData(
    @SerializedName("is_requested")
    val isRequested: Int,
    @SerializedName("request_status")
    val requestStatus: Int,
    @SerializedName("is_alloted")
    val isAllotted: Int
)