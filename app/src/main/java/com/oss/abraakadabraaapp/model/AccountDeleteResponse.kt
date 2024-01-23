package com.oss.abraakadabraaapp.model

import com.google.gson.annotations.SerializedName

data class AccountDeleteResponse(
    @SerializedName("success" ) var success : Boolean? = false,
    @SerializedName("message" ) var message : String?  = ""
)
