package com.oss.abraakadabraaapp.model

import com.google.gson.annotations.SerializedName

class InitChatResponce(
    val code: Int,
    val status: Int,
    val data: InitChatData,
)

data class InitChatData(
    @SerializedName("chat_id")
    val chatId: String = "",
    val product_status: String = "",
    val message: String,
)