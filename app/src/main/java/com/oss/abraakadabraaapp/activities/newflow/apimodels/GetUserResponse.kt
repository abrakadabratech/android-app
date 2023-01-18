package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class GetUserResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("response_message")
    val responseMessage: String? = null,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data") var data   : UsersData? = UsersData()

)

data class UsersData (

    @SerializedName("phone"            ) var phone          : String? = null,
    @SerializedName("social_link_type" ) var socialLinkType : String? = null,
    @SerializedName("social_link"      ) var socialLink     : String? = null,
    @SerializedName("user_avatar"      ) var userAvatar     : String? = null,
    @SerializedName("email"            ) var email          : String? = null,
    @SerializedName("name"             ) var name           : String? = null,
    @SerializedName("uid"              ) var uid            : String? = null,
    @SerializedName("fcmToken"         ) var fcmToken            : String? = null

)