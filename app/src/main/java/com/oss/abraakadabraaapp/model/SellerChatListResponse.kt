package com.oss.abraakadabraaapp.model

import com.google.gson.annotations.SerializedName

class SellerChatListResponse (
    val code: Long,
    val status: Long,
    val data: SellerChatList,
)

data class SellerChatList(
    val pageNumber: Long,
    val pageSize: Long,
    val product: Product1,
    val chats: List<SellerChat>,
)

data class Product1(
    val name: String,
    @SerializedName("posted_at")
    val postedAt: String,
    val image: String,
    val description: String,
)

data class SellerChat(
    @SerializedName("chat_id")
    val chatId: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("product_image")
    val productImage: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("last_message")
    val lastMessage: String,
    @SerializedName("unseen_messages")
    val unseenMessages: Long,
)
