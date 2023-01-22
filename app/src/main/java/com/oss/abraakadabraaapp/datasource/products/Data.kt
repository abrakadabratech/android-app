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
    @SerializedName("products")
    val products: List<Product>
)