package com.oss.abraakadabraaapp.response.mainResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductImage(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("updated_at")
    val updatedAt: String?
): Parcelable