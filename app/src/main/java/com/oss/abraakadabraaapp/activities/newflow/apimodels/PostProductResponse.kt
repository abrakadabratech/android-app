package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

class PostProductResponse(
    @SerializedName("code"             ) var code            : Int?    = null,
    @SerializedName("status"           ) var status          : Int?    = null,
    @SerializedName("response_message" ) var responseMessage : String? = null,
    @SerializedName("data"             ) var data            : PostProductData?   = PostProductData()

)
data class PostProductData (

    @SerializedName("productId" ) var productId : String? = null

)