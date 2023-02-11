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
    @SerializedName("coordinates"   ) var coordinates  : Coordinates?      = Coordinates(),
    @SerializedName("category"      ) var category     : Categories?           = null,
    @SerializedName("cost_saving"   ) var costSaving   : Int?              = null,
    @SerializedName("used_for"      ) var usedFor      : String?           = null,
    @SerializedName("description"   ) var description  : String?           = null,
    @SerializedName("images"        ) var images       : ArrayList<String> = arrayListOf(),
    @SerializedName("name"          ) var name         : String?           = null,
    @SerializedName("condition"     ) var condition    : String?           = null,
    @SerializedName("posted_by"     ) var postedBy     : PostedByUser?   ,
    @SerializedName("location_name" ) var locationName : String?           = null,
    @SerializedName("energy_saving" ) var energySaving : Int?              = null,
    @SerializedName("isReported"    ) var isReported   : Boolean?          = null,
    @SerializedName("isRequested"   ) var isRequested  : Boolean?          = null,
    @SerializedName("requestAccepted"   ) var requestedStatus  : Boolean?          = null,
    @SerializedName("created_at"    ) var createdAt    : String?           = null

)
data class PostedByUser (

    @SerializedName("uid"          ) var uid         : String? = null,
    @SerializedName("name"         ) var name        : String? = null,
    @SerializedName("user_avatar"  ) var userAvatar  : String? = null,
    @SerializedName("member_since" ) var memberSince : String? = null,
    @SerializedName("fcm_token" ) var fcm_token : String? = null


)
data class Coordinates (

    @SerializedName("_latitude"  ) var Latitude  : Double? = null,
    @SerializedName("_longitude" ) var Longitude : Double? = null

)
data class Categories (

    @SerializedName("id"  ) var id  : String? = null,
    @SerializedName("name" ) var name : String? = null

)
