package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName

data class NotificationDataModel(
    @SerializedName("chatNode"        ) var chatNode        : String? = "",
    @SerializedName("notificationDoc" ) var notificationDoc : String? = ""
)