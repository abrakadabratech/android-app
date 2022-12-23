package com.oss.abraakadabraaapp.response.commonResponse


import com.google.gson.annotations.SerializedName

data class HttpErrorResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("response_message")
    val responseMessage: String?,
    @SerializedName("status")
    val status: Boolean?
)