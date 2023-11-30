package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("source"     ) var source    : String? = "",
    @SerializedName("product_id" ) var productId : String? = "",
    @SerializedName("type"       ) var type      : String? = "",
    @SerializedName("message"    ) var message   : String? = ""
)

data class ReportResponce(
    @SerializedName("code"             ) var code            : Int?    = 0,
    @SerializedName("status"           ) var status          : Int?    = 0,
    @SerializedName("response_message" ) var responseMessage : String? = ""
)