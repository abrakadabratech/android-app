package com.oss.abraakadabraaapp.response.mainResponse

import com.google.gson.annotations.SerializedName

data class RequestProductDetailData(
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
        @SerializedName("taker_data")
        val takerData: TakerData,
        @SerializedName("title")
        val title: String,
        @SerializedName("user_id")
        val userId: Int
    ){
        data class TakerData(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("full_address")
            val fullAddress: String?,
            @SerializedName("message")
            val message: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("phone_number")
            val phoneNumber: String,
            @SerializedName("request_status")
            val requestStatus: Int,
            @SerializedName("profile_image")
            val profileImage: String,
        )
    }