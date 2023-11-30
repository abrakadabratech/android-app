package com.oss.abraakadabraaapp.activities.newflow.chat

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class ChatModel(
    @SerializedName("senderId"   ) var senderId   : String? = null,
    @SerializedName("receiverId" ) var receiverId : String? = null,
    @SerializedName("text"       ) var text       : String? = null,
    @SerializedName("from"       ) var from       : String? = null,
    @SerializedName("read"       ) var read       : Boolean = false,
    @SerializedName("timestamp"  ) var timeStamp  : Timestamp? = Timestamp.now()

){constructor():this("","","","")}