package com.oss.abraakadabraaapp.response.productdetails


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PostedBy(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)