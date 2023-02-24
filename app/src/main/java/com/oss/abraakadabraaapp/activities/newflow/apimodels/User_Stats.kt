package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class User_Stats(
    @SerializedName("received"      ) var received     : Int? = 0,
    @SerializedName("rating"        ) var rating       : Int? = 0,
    @SerializedName("given"         ) var given        : Int? = 0,
    @SerializedName("cost_saving"   ) var costSaving   : Int? = 0,
    @SerializedName("energy_saving" ) var energySaving : Int? = 0
)
