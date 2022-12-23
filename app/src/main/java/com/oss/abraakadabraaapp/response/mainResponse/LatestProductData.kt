package com.oss.abraakadabraaapp.response.mainResponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class LatestProductData(
    @SerializedName("distance")
    var distance: Double,
    @SerializedName("full_address")
    var fullAddress: String,
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("image")
    var image: String?,
    @SerializedName("product_id")
    var productId: Int,
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var title: String,
    @SerializedName("is_given")
    var isGiven: Int
) : Parcelable