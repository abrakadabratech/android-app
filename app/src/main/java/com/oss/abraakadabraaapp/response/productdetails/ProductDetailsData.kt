package com.oss.abraakadabraaapp.response.productdetails


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProductDetailsData(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Products,
    @SerializedName("status")
    val status: Int
)
data class Products (

    @SerializedName("id"            ) var id           : String?           = null,
    @SerializedName("images"        ) var images       : ArrayList<String> = arrayListOf(),
    @SerializedName("posted_by"     ) var postedBy     : String?           = null,
    @SerializedName("description"   ) var description  : String?           = null,
    @SerializedName("energy_saving" ) var energySaving : Int?              = null,
    @SerializedName("cost_saving"   ) var costSaving   : Int?              = null,
    @SerializedName("condition"     ) var condition    : String?           = null,
    @SerializedName("category"      ) var category     : String?           = null,
    @SerializedName("name"          ) var name         : String?           = null,
    @SerializedName("location_name" ) var locationName : String?           = null,
    @SerializedName("status"        ) var status       : String?           = null,
    @SerializedName("used_for"      ) var usedFor      : String?           = null,
    @SerializedName("created_at"    ) var createdAt    : String?           = null

)