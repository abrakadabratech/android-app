package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

class OnBoardingResponse(
    @SerializedName("code"    ) var code    : Int?    = 0,
    @SerializedName("status"  ) var status  : Int?    = 0,
    @SerializedName("message" ) var message : String? = ""
)