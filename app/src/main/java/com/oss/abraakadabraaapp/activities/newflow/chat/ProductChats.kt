package com.oss.abraakadabraaapp.activities.newflow.chat

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class ProductChat(
    @SerializedName("product_name") var productName:String = "",
    @SerializedName("product_image") var productImage:String = "",
    @SerializedName("product_id") var productId:String = "",
    @SerializedName("posted_by") var postedBy:String = "",
    @SerializedName("time_stamp") var timeStamp: Timestamp?,
    @SerializedName("chat_list") var chatList: List<ChatListModelV2>? = null,
){
    constructor():this("","","","",null,null)
}
data class ChatListModelV2(
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
    @SerializedName("time_stamp"  ) var time_stamp  : String? = "",
    @SerializedName("date"  ) var date  : String? = "",
    @SerializedName("status"  ) var status  : String? = "",
    @SerializedName("Messages") var messages:List<ChatModel>? = null
){ constructor():this("","","","",
    "","","","","","",null)}
