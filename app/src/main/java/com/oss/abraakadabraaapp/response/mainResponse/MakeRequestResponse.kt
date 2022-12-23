package com.oss.abraakadabraaapp.response.mainResponse


import com.google.gson.annotations.SerializedName

data class MakeRequestResponse(
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
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("product_id")
        val productId: String,
        @SerializedName("user_id")
        val userId: String
    )
}