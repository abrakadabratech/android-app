package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class GetProductDetailResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: ProductDetailData?,
    @SerializedName("developer_message")
    val developerMessage: String,
    @SerializedName("response_message")
    val responseMessage: String,
    @SerializedName("status")
    val status: Boolean
)