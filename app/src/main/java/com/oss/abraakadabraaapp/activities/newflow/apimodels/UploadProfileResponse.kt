package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class UploadProfileResponse(
    @SerializedName("code"             ) var code            : Int?    = null,
    @SerializedName("status"           ) var status          : Int?    = null,
    @SerializedName("response_message" ) var responseMessage : String? = "",
    @SerializedName("data"             ) var data            : Data1
)

data class Data1(
    @SerializedName("userAvatar") var userAvatar:String = ""
)