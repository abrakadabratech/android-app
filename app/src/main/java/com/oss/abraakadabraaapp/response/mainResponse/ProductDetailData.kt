package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class ProductDetailData(
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
    val fullAddress: String,
    @SerializedName("gender_for")
    val genderFor: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("images")
    val images: ArrayList<ProductImage>,
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
    @SerializedName("request_data")
    val requestData: RequestData,
    @SerializedName("title")
    val title: String,
    @SerializedName("user_id")
    val userId: Int ,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("name")
    val userName: String?,
    @SerializedName("profile_image")
    val profileImage: String?,
    @SerializedName("user_full_address")
    val userFullAddress: String?,
    @SerializedName("email")
    val userEmail: String?,
)