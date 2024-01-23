package com.oss.abraakadabraaapp.model

import com.google.gson.annotations.SerializedName

class NotificationResponse(
    @SerializedName("code") var code: Int? = 0,
    @SerializedName("status") var status: Int? = 0,
    @SerializedName("currentPage") var currentPage: Int? = 0,
    @SerializedName("pageSize") var pageSize: Int? = 0,
    @SerializedName("totalPages") var totalPages: Int? = 0,
    @SerializedName("notifications") var notifications: ArrayList<Notifications> = arrayListOf()
)

data class Data(

    @SerializedName("chatNode") var chatNode: String? = "",
    @SerializedName("user_id") var user_id: String? = "",
    @SerializedName("requestId") var requestId: String? = "",
    @SerializedName("product_id") var product_id: String? = "",
    @SerializedName("notificationDoc") var notificationDoc: String? = "",
    @SerializedName("request_status") var requestStatus: String? = ""

)


data class Notifications(

    @SerializedName("id") var id: String? = null,
    @SerializedName("deleted") var deleted: Boolean? = null,
    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("docId") var docId: String? = null,
    @SerializedName("module") var module: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("isSelect") var isSelect:Boolean = false

)

data class ReadNotificationResponse(
    @SerializedName("code"    ) var code    : Int?    = null,
    @SerializedName("status"  ) var status  : Int?    = null,
    @SerializedName("message" ) var message : String? = null
)

data class DeleteAll(
    @SerializedName("all") var all : Boolean
)

data class DeleteMultiple(
    @SerializedName("notifications") var notifications:List<String>
)
