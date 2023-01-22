package com.oss.abraakadabraaapp.datasource.products


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GetProducts(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Int
)