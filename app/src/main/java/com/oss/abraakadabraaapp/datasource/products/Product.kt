package com.oss.abraakadabraaapp.datasource.products


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Product(
    @SerializedName("condition")
    val condition: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("is_self_product")
    val isSelfProduct: Boolean,
    @SerializedName("display_image")
    val display_image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("location_name" ) var locationName : String? = null
    )
