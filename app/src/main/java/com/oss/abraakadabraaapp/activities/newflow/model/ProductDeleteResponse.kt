package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName

data class ProductDeleteResponse(
    @SerializedName("code"             ) var code            : Int?    = null,
    @SerializedName("status"           ) var status          : Int?    = null,
    @SerializedName("response_message" ) var responseMessage : String? = null,
    @SerializedName("data") var data:Dataa
)
data class Dataa(
    @SerializedName("request_status") var request_status:String? = null,
    @SerializedName("product_status") var product_status:String? = ""
)
