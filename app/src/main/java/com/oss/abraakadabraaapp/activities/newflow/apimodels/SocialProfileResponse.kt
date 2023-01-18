package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class SocialProfileResponse(
    @SerializedName("code"             ) var code            : Int?    = null,
    @SerializedName("status"           ) var status          : Int?    = null,
    @SerializedName("response_message" ) var responseMessage : String? = null,
    @SerializedName("data"             ) var data            : SocialData?   = SocialData()
)
data class SocialData (

    @SerializedName("social_link") var socialLink     : String? = null,
    @SerializedName("social_link_type" ) var socialLinkType : String? = null

)