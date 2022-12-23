package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class CategoryData(
    @SerializedName("category_image")
    val categoryImage: String,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("image")
    val image: Int,// changing to int from String
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("updated_at")
    val updatedAt: String?
){
    override fun toString(): String {
        return categoryName
    }
}