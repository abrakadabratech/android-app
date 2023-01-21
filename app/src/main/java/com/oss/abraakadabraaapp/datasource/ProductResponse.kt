package com.oss.abraakadabraaapp.datasource

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProductResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Int
)

@Keep
data class Product(
    @SerializedName("condition")
    val condition: String,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    var location  : Location? = Location(),
)

@Keep
data class Data(
    @SerializedName("count")
    val count: Int,
    @SerializedName("page")
    val page: String,
    @SerializedName("products")
    val products: List<Product>
)

@Keep
data class Location (
    @SerializedName("_latitude")
    var Latitude  : Double? = null,
    @SerializedName("_longitude")
    var Longitude : Double? = null
)