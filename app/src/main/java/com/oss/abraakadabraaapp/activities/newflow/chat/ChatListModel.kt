package com.oss.abraakadabraaapp.activities.newflow.chat

import com.google.gson.annotations.SerializedName

data class ChatListModel(
    @SerializedName("from"   ) var from   : String? = null,
    @SerializedName("product_id" ) var product_id : String? = null,
    @SerializedName("product"   ) var product   : String? = null,
    @SerializedName("receiver_avatar"       ) var receiver_avatar : String? = null,
    @SerializedName("receiver_id"       ) var receiver_id : String? = null,
    @SerializedName("receiver_name"  ) var receiver_name  : String? = null,
    @SerializedName("sender_avatar"  ) var sender_avatar  : String? = null,
    @SerializedName("sender_id"  ) var sender_id  : String? = null,
    @SerializedName("sender_name"  ) var sender_name  : String? = null,
    @SerializedName("last_message"  ) var last_message  : String? = null,
    @SerializedName("time_stamp"  ) var time_stamp  : String? = null,
    @SerializedName("date"  ) var date  : String? = null,
    @SerializedName("Messages") var messages:List<ChatModel>? = null
){constructor():this("","","","",
    "","","","","","",null)}
