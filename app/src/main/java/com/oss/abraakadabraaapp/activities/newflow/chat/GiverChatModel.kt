package com.oss.abraakadabraaapp.activities.newflow.chat

import com.google.gson.annotations.SerializedName

class GiverChatModel (
    @SerializedName("product"       ) var product      : String? = null,
    @SerializedName("receiver_id"   ) var receiverId   : String? = null,
    @SerializedName("product_id"    ) var productId    : String? = null,
    @SerializedName("receiver_name" ) var receiverName : String? = null,
    @SerializedName("sender_id"     ) var senderId     : String? = null
)