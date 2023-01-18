package com.oss.abraakadabraaapp.datasource

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductResponse(
    @SerializedName("code"   ) var code   : Int?  = null,
    @SerializedName("status" ) var status : Int?  = null,
    @SerializedName("data"   ) var data   : Data? = Data()
) : Parcelable

@Parcelize
data class Data (

    @SerializedName("page"     ) var page     : Int?                = null,
    @SerializedName("count"    ) var count    : Int?                = null,
    @SerializedName("products" ) var products : List<Products> = arrayListOf()

) : Parcelable

@Parcelize
data class Products (

    @SerializedName("id"        ) var id        : String?   = null,
    @SerializedName("image_url" ) var imageUrl  : String?   = null,
    @SerializedName("condition" ) var condition : String?   = null,
    @SerializedName("location"  ) var location  : Location? = Location(),
    @SerializedName("name"      ) var name      : String?   = null
) : Parcelable

@Parcelize
data class Location (

    @SerializedName("_latitude"  ) var Latitude  : Double? = null,
    @SerializedName("_longitude" ) var Longitude : Double? = null

) : Parcelable