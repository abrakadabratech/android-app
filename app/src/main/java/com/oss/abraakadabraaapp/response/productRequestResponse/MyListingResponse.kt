package com.oss.abraakadabraaapp.response.productRequestResponse

import com.google.gson.annotations.SerializedName

data class MyListingResponse(
    @SerializedName("code"   ) var code   : Int?            = null,
    @SerializedName("status" ) var status : Int?            = null,
    @SerializedName("data"   ) var data   : ArrayList<RequestData> = arrayListOf()
)
data class RequestData (

    @SerializedName("id"         ) var id        : String? = null,
    @SerializedName("name"       ) var name      : String? = null,
    @SerializedName("image"      ) var image     : String? = null,
    @SerializedName("status"     ) var status    : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("responses"  ) var responses : Int?    = null,
    @SerializedName("type"    ) var type    : String?           = "free",
    @SerializedName("price"    ) var price    : Int?           = 0,
    @SerializedName("currency"    ) var currency    : String?           = "INR"

)
