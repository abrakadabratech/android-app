package com.oss.abraakadabraaapp.response.productRequestResponse

import com.google.gson.annotations.SerializedName

data class ListingResponse (

    @SerializedName("code"     ) var code     : Int?              = null,
    @SerializedName("status"   ) var status   : Int?              = null,
    @SerializedName("alert_message"  ) var alertMessage  : AlertMessage          = AlertMessage(),
    @SerializedName("product"  ) var product  : ListingProduct?          = ListingProduct(),
    @SerializedName("requests" ) var requests : ArrayList<Requests> = arrayListOf(),
    @SerializedName("total_requests" ) var total_requests : Int = 0,


)

data class PostedBy (

    @SerializedName("id"   ) var id   : String? = null,
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("user_avatar" ) var user_avatar : String? = null

)

data class Coordinates (

    @SerializedName("_latitude"  ) var Latitude  : Double? = null,
    @SerializedName("_longitude" ) var Longitude : Double? = null

)

data class ListingProduct (

    @SerializedName("id"            ) var id     : String?           = null,
    @SerializedName("category"      ) var category     : Category?           = null,
    @SerializedName("images"        ) var images       : ArrayList<String> = arrayListOf(),
    @SerializedName("condition"     ) var condition    : String?           = null,
    @SerializedName("name"          ) var name         : String?           = null,
    @SerializedName("energy_saving" ) var energySaving : Int?              = null,
    @SerializedName("cost_saving"   ) var costSaving   : Int?              = null,
    @SerializedName("price"         ) var price   : Int?              = null,
    @SerializedName("brand"         ) var brand   : String?              = null,
    @SerializedName("status"        ) var status       : String?           = null,
    @SerializedName("used_for"      ) var usedFor      : String?           = null,
    @SerializedName("posted_by"     ) var postedBy     : PostedBy?         = PostedBy(),
    @SerializedName("description"   ) var description  : String?           = null,
    @SerializedName("coordinates"   ) var coordinates  : Coordinates?      = Coordinates(),
    @SerializedName("location_name" ) var locationName : String?           = null,
    @SerializedName("created_at"    ) var createdAt    : String?           = null,
    @SerializedName("type"          ) var type    : String?           = "free",
    @SerializedName("currency"      ) var currency    : String?           = "INR"

)
data class Requests (

    @SerializedName("request_id"   ) var requestId   : String?      = null,
    @SerializedName("timestamp"    ) var timestamp   : Int?         = null,
    @SerializedName("distance"    ) var distance   : Int?         = null,
    @SerializedName("status"       ) var status      : String?      = null,
    @SerializedName("coordinates"  ) var coordinates : Coordinates? = Coordinates(),
    @SerializedName("productId"    ) var productId   : String?      = null,
    @SerializedName("message"      ) var message     : String?      = null,
    @SerializedName("userId"       ) var userId      : String?      = null,
    @SerializedName("username"     ) var username    : String?      = null,
    @SerializedName("user_avatar" ) var user_avatar : String? = null,
    @SerializedName("isReceived" ) var isReceived : Boolean?           = false,
    @SerializedName("isDelivered" ) var isDelivered : Boolean?           = false,
    @SerializedName("email"        ) var email       : String?      = null,
    @SerializedName("phone"        ) var phone       : String?      = null,
    @SerializedName("requested_at" ) var requestedAt : String?      = null,
    @SerializedName("chat_id" ) var chatId : String?      = null
)

data class Category (

    @SerializedName("id"   ) var id   : String? = null,
    @SerializedName("name" ) var name : String? = null

)
data class AlertMessage(
    @SerializedName("type"   ) var type   : String = "",
    @SerializedName("message" ) var message : String = "Data message to be displayed"

)