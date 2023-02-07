package com.oss.abraakadabraaapp.activities.newflow.chat

import com.google.gson.annotations.SerializedName

data class ChatModel(
    @SerializedName("senderId"   ) var senderId   : String? = null,
    @SerializedName("receiverId" ) var receiverId : String? = null,
    @SerializedName("text"       ) var text       : String? = null,
    @SerializedName("from"       ) var from       : String? = null,
    @SerializedName("timestamp"  ) var timestamp  : String? = null
){constructor():this("","","","")}