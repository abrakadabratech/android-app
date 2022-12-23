package com.oss.abraakadabraaapp.response.authResponse


import com.google.gson.annotations.SerializedName
import com.oss.abraakadabraaapp.model.UserData


data class SignInResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: UserData?,
    @SerializedName("developer_message")
    val developerMessage: String,
    @SerializedName("response_message")
    val responseMessage: String,
    @SerializedName("status")
    val status: Boolean
)