package com.oss.abraakadabraaapp.activities.newflow.requests

import com.google.gson.annotations.SerializedName

data class ReportProductRequest(
    @SerializedName("productId" ) var productId : String? = null,
    @SerializedName("type"      ) var type      : String? = null,
    @SerializedName("message"   ) var message   : String? = null
)
