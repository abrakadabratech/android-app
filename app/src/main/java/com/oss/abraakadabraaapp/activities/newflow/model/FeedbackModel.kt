package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName

data class FeedbackModel(
    @SerializedName("code"     ) var code     : Int?    = null,
    @SerializedName("status"   ) var status   : Int?    = null,
    @SerializedName("response" ) var response : String? = null
)