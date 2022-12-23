package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class GetRequestProductDetailResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: RequestProductDetailData?,
    @SerializedName("developer_message")
    val developerMessage: String,
    @SerializedName("response_message")
    val responseMessage: String,
    @SerializedName("status")
    val status: Boolean
)