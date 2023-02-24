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

    @SerializedName("rating"        ) var rating       : Int? = null,
    @SerializedName("energy_saving" ) var energySaving : Int? = null,
    @SerializedName("received"      ) var received     : Int? = null,
    @SerializedName("cost_saving"   ) var costSaving   : Int? = null,
    @SerializedName("given"         ) var given        : Int? = null

)
data class UsersData (

    @SerializedName("phone"            ) var phone          : String? = null,
    @SerializedName("status"            ) var status          : String? = null,
    @SerializedName("social_link_type" ) var socialLinkType : String? = null,
    @SerializedName("social_link"      ) var socialLink     : String? = null,
    @SerializedName("user_avatar"      ) var userAvatar     : String? = null,
    @SerializedName("email"            ) var email          : String? = null,
    @SerializedName("name"             ) var name           : String? = null,
    @SerializedName("user_stats"       ) var userStats      : UserStats? = UserStats(),
    @SerializedName("uid"              ) var uid            : String? = null,
    @SerializedName("location"              ) var location            : String? = null,
    @SerializedName("fcmToken"         ) var fcmToken            : String? = null
) {constructor():this("","","","",
    "","","",null,"","","")}

