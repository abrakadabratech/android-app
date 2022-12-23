package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class ReceiverData(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("request_status")
    val requestStatus: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("request_at")
    val requestAt: String,
)