package com.oss.abraakadabraaapp.response.productRequestResponse

import com.google.gson.annotations.SerializedName

data class MyRequestResponse(
    @SerializedName("code"   ) var code   : Int?            = null,
    @SerializedName("status" ) var status : Int?            = null,
    @SerializedName("data"   ) var data   : ArrayList<Data> = arrayListOf()
)
data class Product (

    @SerializedName("id"    ) var id    : String? = null,
    @SerializedName("name"  ) var name  : String? = null,
    @SerializedName("image" ) var image : String? = null

)
data class Data (
    @SerializedName("id"           ) var id          : String?  = null,
    @SerializedName("status"       ) var status      : String?  = null,
    @SerializedName("message"      ) var message     : String?  = null,
    @SerializedName("product"      ) var product     : Product? = Product(),
    @SerializedName("requested_at" ) var requestedAt : String?  = null,
    @SerializedName("posted_by"     ) var postedBy     : PostedByUser1?,
)
data class PostedByUser1 (

    @SerializedName("uid"          ) var uid         : String? = null,
    @SerializedName("name"         ) var name        : String? = null,
    @SerializedName("user_avatar"  ) var userAvatar  : String? = null,
    @SerializedName("member_since" ) var memberSince : String? = null,
    @SerializedName("fcm_token" ) var fcm_token : String? = null

)