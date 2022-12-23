package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class RequestActionResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("developer_message")
    val developerMessage: String,
    @SerializedName("response_message")
    val responseMessage: String,
    @SerializedName("status")
    val status: Boolean
){
    data class Data(
        @SerializedName("request_status")
        val requestStatus: String
    )
}