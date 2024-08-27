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
    @SerializedName("product_status") var product_status: String? = "",
    @SerializedName("request_status") var requestStatus: String? = ""

)


data class Notifications(

    @SerializedName("id") var id: String? = "",
    @SerializedName("deleted") var deleted: Boolean? = false,
    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("docId") var docId: String? = "",
    @SerializedName("module") var module: String? = "",
    @SerializedName("title") var title: String? = "",
    @SerializedName("body") var body: String? = "",
    @SerializedName("userId") var userId: String? = "",
    @SerializedName("timestamp" ) var timestamp : TimeStamp? = TimeStamp(),
    @SerializedName("isSelect") var isSelect:Boolean = false

)

data class ReadNotificationResponse(
    @SerializedName("code"    ) var code    : Int?    = 0,
    @SerializedName("status"  ) var status  : Int?    = 0,
    @SerializedName("message" ) var message : String? = ""
)

data class DeleteAll(
    @SerializedName("all") var all : Boolean
)

data class DeleteMultiple(
    @SerializedName("notifications") var notifications:List<String>
)

data class TimeStamp(
    @SerializedName("_seconds"     ) var Seconds     : Int? = 0,
    @SerializedName("_nanoseconds" ) var Nanoseconds : Int? = 0
)