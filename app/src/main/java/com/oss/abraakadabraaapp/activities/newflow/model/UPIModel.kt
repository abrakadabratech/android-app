package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName

data class UPIModel(
    @SerializedName("status"      ) var status      : String? = "",
    @SerializedName("message"     ) var message     : String? = "",
    @SerializedName("userMessage" ) var userMessage : String? = ""
)
