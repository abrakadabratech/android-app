package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName

data class InitPaymentModel(
    @SerializedName("code"             ) var code            : Int?    = null,
    @SerializedName("status"           ) var status          : Int?    = null,
    @SerializedName("response_message" ) var responseMessage : String? = null,
    @SerializedName("data"             ) var data            : Data?   = Data()
)
data class Data (

    @SerializedName("orderId" ) var orderId : String? = null,
    @SerializedName("amount"  ) var amount  : Int?    = null

)