package com.oss.abraakadabraaapp.response.notificationResponse


import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: ArrayList<NotificationData>,
    @SerializedName("developer_message")
    val developerMessage: String,
    @SerializedName("response_message")
    val responseMessage: String,
    @SerializedName("status")
    val status: Boolean
){
    data class NotificationData(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("module")
        val module: String,
        @SerializedName("module_id")
        val moduleId: Int,
        @SerializedName("notification_title")
        val notificationTitle: String,
        @SerializedName("profile_image")
        val profileImage: String,
        @SerializedName("role")
        val role: Int,
        @SerializedName("second_user_id")
        val secondUserId: String,
        @SerializedName("seen_status")
        var seenStatus: Int,
        @SerializedName("module_data_2")
        var moduleData2: Int,//product id
        @SerializedName("module_data")
        var moduleData: Int,
        @SerializedName("type")
        val type: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("user_name")
        val userName: String
    )
}