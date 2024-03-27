package com.oss.abraakadabraaapp.response.productRequestResponse

import com.google.gson.annotations.SerializedName

class RequestorResponse(
    @SerializedName("code"   ) var code   : Int?  = null,
    @SerializedName("status" ) var status : Int?  = null,
    @SerializedName("data"   ) var data   : Data1? = Data1()

)
data class Data1 (

    @SerializedName("request_id"    ) var requestId    : String?       = null,
    @SerializedName("product"       ) var product      : Product1?      = Product1(),
    @SerializedName("receiver_info" ) var receiverInfo : ReceiverInfo? = ReceiverInfo(),
    @SerializedName("request"       ) var request      : Request?      = Request()

)
data class Category1 (

    @SerializedName("id"   ) var id   : String? = null,
    @SerializedName("name" ) var name : String? = null

)
data class Product1 (

    @SerializedName("product_id"    ) var productId    : String?           = null,
    @SerializedName("name"          ) var name         : String?           = null,
    @SerializedName("brand"         ) var brand        : String?           = null,
    @SerializedName("posted_by"     ) var postedBy     : String?           = null,
    @SerializedName("category"      ) var category     : Category1?         = Category1(),
    @SerializedName("location_name" ) var locationName : String?           = null,
    @SerializedName("description"   ) var description  : String?           = null,
    @SerializedName("condition"     ) var condition    : String?           = null,
    @SerializedName("cost_saving"   ) var costSaving   : Int?              = null,
    @SerializedName("used_for"      ) var usedFor      : String?           = null,
    @SerializedName("status"        ) var status       : String?           = "",
    @SerializedName("images"        ) var images       : ArrayList<String> = arrayListOf(),
    @SerializedName("energy_saving" ) var energySaving : Int?              = null

)
data class ReceiverInfo (

    @SerializedName("phone"       ) var phone      : String? = null,
    @SerializedName("user_avatar" ) var userAvatar : String? = null,
    @SerializedName("email"       ) var email      : String? = null,
    @SerializedName("name"        ) var name       : String? = null

)
data class Timestamp (

    @SerializedName("_seconds"     ) var Seconds     : Int? = null,
    @SerializedName("_nanoseconds" ) var Nanoseconds : Int? = null

)

data class Coordinates1 (

    @SerializedName("_latitude"  ) var Latitude  : Double? = null,
    @SerializedName("_longitude" ) var Longitude : Double? = null

)
data class Request (

    @SerializedName("userId"      ) var userId      : String?      = null,
    @SerializedName("timestamp"   ) var timestamp   : Timestamp?   = Timestamp(),
    @SerializedName("status"      ) var status      : String?      = null,
    @SerializedName("coordinates" ) var coordinates : Coordinates1? = Coordinates1(),
    @SerializedName("isDelivered" ) var isDelivered : Boolean?     = null,
    @SerializedName("isReceived"  ) var isReceived  : Boolean?     = null,
    @SerializedName("message"     ) var message     : String?      = null,
    @SerializedName("chatNode"     ) var chatNode     : String?      = ""

)