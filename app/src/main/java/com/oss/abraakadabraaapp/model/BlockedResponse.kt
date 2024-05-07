package com.oss.abraakadabraaapp.model

data class BlockedResponse(
    val code:String = "",
    val statu:String = "",
    val data: RData
)
data class RData(
    val sender_id :String ="",
    val receiver_id :String ="",
    val is_blocked :Boolean =false,
)