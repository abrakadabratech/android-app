package com.oss.abraakadabraaapp.activities.newflow.menu

import com.google.gson.annotations.SerializedName

data class SupportResponse(
    @SerializedName("code"   ) var code   : Int?  = null,
    @SerializedName("status" ) var status : Int?  = null,
    @SerializedName("data"   ) var data   : SupportData? = SupportData()

)

data class SupportData (

    @SerializedName("version" ) var version : String? = null,
    @SerializedName("phone"   ) var phone   : String? = null,
    @SerializedName("email"   ) var email   : String? = null

)
