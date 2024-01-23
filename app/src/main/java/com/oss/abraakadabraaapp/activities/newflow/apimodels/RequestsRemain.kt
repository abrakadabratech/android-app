package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class RequestsRemain (

    @SerializedName("user_id"         ) var userId         : String? = "",
    @SerializedName("timestamp"       ) var timestamp      : String? = "",
    @SerializedName("request_allowed" ) var requestAllowed : Boolean = true,
    @SerializedName("error_code"      ) var errorCode      : String? = null,
    @SerializedName("user_message"    ) var userMessage    : String? = null


)