package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName

class RazorPayModel(
    @SerializedName("code"   ) var code   : Int?  = null,
    @SerializedName("status" ) var status : Int?  = null,
    @SerializedName("data"   ) var data   : R_Data? = R_Data()
)
data class R_Data (

    @SerializedName("RAZORPAY_KEY_ID" ) var RAZORPAYKEYID : String? = null

)