package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class CreateUserRequest(
    @SerializedName("method") var method:String,
    @SerializedName("idToken") var idToken:String
)
