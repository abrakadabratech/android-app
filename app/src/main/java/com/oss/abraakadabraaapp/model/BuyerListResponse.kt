package com.oss.abraakadabraaapp.model

import com.google.gson.annotations.SerializedName

class BuyerListResponse(
    val code: Long,
    val status: Long,
    val data: BuyerList,
)

data class BuyerList(
    val pageNumber: Long,
    val pageSize: Long,
    val chats: List<Chat>,
)

data class Chat(
    @SerializedName("chat_id")
    val chatId: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("product_image")
    val productImage: String,
    @SerializedName("product_status")
    val product_status:String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("last_message")
    val lastMessage: String,
    @SerializedName("unseen_messages")
    val unseenMessages: Long,
)
