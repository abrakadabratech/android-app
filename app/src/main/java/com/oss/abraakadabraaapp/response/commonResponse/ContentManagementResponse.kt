package com.oss.abraakadabraaapp.response.commonResponse


import com.google.gson.annotations.SerializedName

data class ContentManagementResponse(
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
        @SerializedName("content")
        val content: Content
    ) {
        data class Content(
            @SerializedName("type")
            val type: String,
            @SerializedName("value")
            val value: String
        )
    }
}