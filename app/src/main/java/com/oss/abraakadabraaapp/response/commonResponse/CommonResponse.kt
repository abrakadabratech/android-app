package com.oss.abraakadabraaapp.response.commonResponse

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Any?,
    @SerializedName("developer_message")
    val developerMessage: String,
    @SerializedName("response_message")
    val responseMessage: String,
    @SerializedName("status")
    val status: Boolean
)