package com.oss.abraakadabraaapp.response.authResponse


import com.google.gson.annotations.SerializedName

data class SendOtpResponse(
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
        @SerializedName("otp_code")
        val otpCode: String,
        @SerializedName("phone_number")
        val phoneNumber: String
    )
}