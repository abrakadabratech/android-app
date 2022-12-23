package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class GetHomeDataResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("developer_message")
    val developerMessage: String,
    @SerializedName("response_message")
    val responseMessage: String,
    @SerializedName("status")
    val status: Boolean
){
    data class Data(
        @SerializedName("category")
        val category: ArrayList<CategoryData>,
        @SerializedName("products")
        val products: ArrayList<LatestProductData>,
        @SerializedName("notification_count")
        val notificationCount: Int
    )
}