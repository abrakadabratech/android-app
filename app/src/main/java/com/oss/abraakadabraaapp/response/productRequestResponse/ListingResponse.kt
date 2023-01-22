package com.oss.abraakadabraaapp.response.productRequestResponse

import com.google.gson.annotations.SerializedName

data class ListingResponse (
    @SerializedName("code"     ) var code     : Int?              = null,
    @SerializedName("status"   ) var status   : Int?              = null,
    @SerializedName("product"  ) var product  : ListingProduct?          = ListingProduct(),
    @SerializedName("requests" ) var requests : ArrayList<String> = arrayListOf()
)

data class PostedBy (

    @SerializedName("id"   ) var id   : String? = null,
    @SerializedName("name" ) var name : String? = null

)

data class Coordinates (

    @SerializedName("_latitude"  ) var Latitude  : Double? = null,
    @SerializedName("_longitude" ) var Longitude : Double? = null

)

data class ListingProduct (

    @SerializedName("category"      ) var category     : String?           = null,
    @SerializedName("images"        ) var images       : ArrayList<String> = arrayListOf(),
    @SerializedName("condition"     ) var condition    : String?           = null,
    @SerializedName("energy_saving" ) var energySaving : Int?              = null,
    @SerializedName("cost_saving"   ) var costSaving   : Int?              = null,
    @SerializedName("status"        ) var status       : String?           = null,
    @SerializedName("name"          ) var name         : String?           = null,
    @SerializedName("used_for"      ) var usedFor      : String?           = null,
    @SerializedName("posted_by"     ) var postedBy     : PostedBy?         = PostedBy(),
    @SerializedName("description"   ) var description  : String?           = null,
    @SerializedName("coordinates"   ) var coordinates  : Coordinates?      = Coordinates(),
    @SerializedName("location_name" ) var locationName : String?           = null,
    @SerializedName("created_at"    ) var createdAt    : String?           = null

)
