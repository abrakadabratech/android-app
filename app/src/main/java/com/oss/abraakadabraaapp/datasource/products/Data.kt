package com.oss.abraakadabraaapp.datasource.products


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.oss.abraakadabraaapp.datasource.products.Product

@Keep
data class Data(
    @SerializedName("count")
    val count: Int,
    @SerializedName("page")
    val page: String,
    @SerializedName("category" ) var category : ArrayList<String>   = arrayListOf(),
    @SerializedName("products")
    val products: ArrayList<Product>
)