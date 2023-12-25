package com.oss.abraakadabraaapp.activities.newflow.chat

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class ChatListModel(
    @SerializedName("from"   ) var from   : String? = "",
    @SerializedName("product_id" ) var product_id : String? = "",
    @SerializedName("product"   ) var product   : String? = "",
    @SerializedName("product_giver"   ) var product_giver   : String? = "",
    @SerializedName("product_receiver"   ) var product_receiver   : String? = "",
    @SerializedName("receiver_avatar"       ) var receiver_avatar : String? = "",
    @SerializedName("receiver_id"       ) var receiver_id : String? = "",
    @SerializedName("receiver_name"  ) var receiver_name  : String? = "",
    @SerializedName("sender_avatar"  ) var sender_avatar  : String? = "",
    @SerializedName("sender_id"  ) var sender_id  : String? = "",
    @SerializedName("sender_name"  ) var sender_name  : String? = "",
    @SerializedName("last_message"  ) var last_message  : String? = "",
//    @SerializedName("time_stamp"  ) var time_stamp  : String= "",
    @SerializedName("time_stamp"  ) var time_stamp  : Any,
    @SerializedName("date"  ) var date  : String? = "",
    @SerializedName("status"  ) var status  : String? = "accepted",
    @SerializedName("Messages") var messages:List<ChatModel>? = null,
    @SerializedName("request_id") var requestId:String = "",
    @SerializedName("product_image") var product_image:String = "",
    @SerializedName("enabled") var enabled:Boolean = true
){ constructor():this("","","","",
    "","","","","",
    "","","","")}

data class GroupedChatListModel(
    @Transient
    var isListShown:Boolean = false,
    var product_id:String,
    var product_name:String,
    var posted_by:String,
    var product_url:String,
    var chats:List<ChatListModel>
)