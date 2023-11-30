package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName

data class NotificationDataModel(
    @SerializedName("chatNode"        ) var chatNode        : String? = "",
    @SerializedName("user_id"        ) var user_id        : String? = "",
    @SerializedName("requestId"        ) var requestId        : String? = "",
    @SerializedName("product_id"        ) var product_id        : String? = "",
    @SerializedName("notificationDoc" ) var notificationDoc : String? = "",
    @SerializedName("request_status" ) var requestStatus : String? = ""
)

data class BroadCastNotificationModel(
    @SerializedName("user_id"        ) var user_id        : String? = "",
    @SerializedName("product_id"        ) var product_id        : String? = ""
)