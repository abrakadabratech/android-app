package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("code"             ) var code            : Int?    = null,
    @SerializedName("status"           ) var status          : Int?    = null,
    @SerializedName("response_message" ) var responseMessage : String? = null

)