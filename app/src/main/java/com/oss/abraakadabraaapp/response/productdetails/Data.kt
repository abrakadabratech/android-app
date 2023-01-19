package com.oss.abraakadabraaapp.response.productdetails


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Data(
    @SerializedName("category")
    val category: String,
    @SerializedName("condition")
    val condition: String,
    @SerializedName("cost_saving")
    val costSaving: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("energy_saving")
    val energySaving: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("location_name")
    val locationName: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("posted_by")
    val postedBy: PostedBy,
    @SerializedName("used_for")
    val usedFor: String
)