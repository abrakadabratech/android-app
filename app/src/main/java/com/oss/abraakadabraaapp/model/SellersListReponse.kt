package com.oss.abraakadabraaapp.model

import com.google.gson.annotations.SerializedName

class SellersListReponse(
    val code: Long,
    val status: Long,
    val data: SellerList,
)

data class SellerList(
    val pageNumber: Long,
    val pageSize: Long,
    val products: List<Product>,
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val product_status:String,
    @SerializedName("product_image")
    val productImage: String,
    val timestamp: Any,
    @SerializedName("chat_count")
    val chatCount: String,
    @SerializedName("unseen_chats")
    val unseenChats: Int,
)
data class Timestamp2 (

    @SerializedName("_seconds"     ) var Seconds     : Int? = null,
    @SerializedName("_nanoseconds" ) var Nanoseconds : Int? = null

)
