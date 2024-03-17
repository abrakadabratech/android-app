package com.oss.abraakadabraaapp.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("code"   ) var code   : Int?  = null,
    @SerializedName("status" ) var status : Int?  = null,
    @SerializedName("data"   ) var data   : Data1? = Data1()
)

data class User (

    @SerializedName("name"      ) var name     : String? = null,
    @SerializedName("photo"     ) var photo    : String? = null,
    @SerializedName("joined_at" ) var joinedAt : Int?    = null

)

data class Timestamp1 (

    @SerializedName("_seconds"     ) var Seconds     : Int? = null,
    @SerializedName("_nanoseconds" ) var Nanoseconds : Int? = null

)

data class RecentProducts (

    @SerializedName("id"            ) var id           : String?    = null,
    @SerializedName("price"         ) var price        : Int?       = null,
    @SerializedName("name"          ) var name         : String?    = null,
    @SerializedName("type"          ) var type         : String?    = null,
    @SerializedName("display_image" ) var displayImage : String?    = null,
    @SerializedName("timestamp"     ) var timestamp    : Timestamp1? = Timestamp1(),
    @SerializedName("status"        ) var status       : String?    = null

)

data class Data1 (

    @SerializedName("user"                  ) var user                : User?                     = User(),
    @SerializedName("total_products_posted" ) var totalProductsPosted : Int?                      = null,
    @SerializedName("recent_products"       ) var recentProducts      : ArrayList<RecentProducts> = arrayListOf(),
    @SerializedName("reviews"               ) var reviews             : ArrayList<String>         = arrayListOf()

)
