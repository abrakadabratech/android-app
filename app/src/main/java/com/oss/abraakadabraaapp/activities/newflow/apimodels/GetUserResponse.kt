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
data class UserStats (

    @SerializedName("rating"        ) var rating       : Int? = 0,
    @SerializedName("energy_saving" ) var energySaving : Int? = 0,
    @SerializedName("received"      ) var received     : Int? = 0,
    @SerializedName("cost_saving"   ) var costSaving   : Int? = 0,
    @SerializedName("given"         ) var given        : Int? = 0

)
data class UsersData (

    @SerializedName("phone"            ) var phone          : String? = "",
    @SerializedName("status"            ) var status          : String? = "active",
    @SerializedName("social_link_type" ) var socialLinkType : String? = "",
    @SerializedName("social_link"      ) var socialLink     : String? = "",
    @SerializedName("user_avatar"      ) var userAvatar     : String? = "",
    @SerializedName("email"            ) var email          : String? = "",
    @SerializedName("name"             ) var name           : String? = "",
    @SerializedName("user_stats"       ) var userStats      : UserStats? = UserStats(),
    @SerializedName("uid"              ) var uid            : String? = "",
    @SerializedName("location"              ) var location            : String? = "",
    @SerializedName("fcmToken"         ) var fcmToken            : String? = ""
) {constructor():this("","","","",
    "","","",null,"","","")}

