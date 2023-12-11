package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class BannerResponce (
    @SerializedName("code"   ) var code   : Int?            = 0,
    @SerializedName("status" ) var status : Int?            = 0,
    @SerializedName("data"   ) var data   : ArrayList<BannerData> = arrayListOf()
)

data class BannerData(
    @SerializedName("id"        ) var id       : String? = "",
    @SerializedName("title"     ) var title    : String? = "",
    @SerializedName("image" ) var imageUrl : String? = "",
    @SerializedName("height"    ) var height   : Int?    = 0,
    @SerializedName("width"     ) var width    : Int?    = 0
)