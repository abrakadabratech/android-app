package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class GetCategoryResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: ArrayList<CategoryData>,
    @SerializedName("developer_message")
    val developerMessage: String,
    @SerializedName("response_message")
    val responseMessage: String,
    @SerializedName("status")
    val status: Boolean
)