package com.oss.abraakadabraaapp.response.productdetails


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProductDetailsData(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Int
)