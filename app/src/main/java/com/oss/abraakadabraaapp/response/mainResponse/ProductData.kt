package com.oss.abraakadabraaapp.response.mainResponse

import com.google.gson.annotations.SerializedName

data class ProductData(
    @SerializedName("brand")
    val brand: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("full_address")
    var fullAddress: String,
    @SerializedName("gender_for")
    val genderFor: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    var image: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String,
    @SerializedName("product_age")
    val productAge: String,
    @SerializedName("product_condition")
    val productCondition: String?,
    @SerializedName("quality")
    val quality: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("is_given")
    var isGiven: Int
)