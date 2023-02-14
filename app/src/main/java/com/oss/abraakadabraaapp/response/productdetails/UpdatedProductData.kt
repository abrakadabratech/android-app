import com.google.gson.annotations.SerializedName

data class UpdatedProductData(
    @SerializedName("code"             ) var code            : Int?    = null,
    @SerializedName("status"           ) var status          : Int?    = null,
    @SerializedName("response_message" ) var responseMessage : String? = null,
    @SerializedName("data"             ) var data            : UpdatedData?   = UpdatedData()
)
data class UpdatedCoordinates (

    @SerializedName("_latitude"  ) var Latitude  : Double? = null,
    @SerializedName("_longitude" ) var Longitude : Double? = null

)

data class UpdatedData (

    @SerializedName("used_for"      ) var usedFor      : String?           = null,
    @SerializedName("coordinates"   ) var coordinates  : Coordinates?      = Coordinates(),
    @SerializedName("name"          ) var name         : String?           = null,
    @SerializedName("cost_saving"   ) var costSaving   : Int?              = null,
    @SerializedName("condition"     ) var condition    : String?           = null,
    @SerializedName("description"   ) var description  : String?           = null,
    @SerializedName("location_name" ) var locationName : String?           = null,
    @SerializedName("images"        ) var images       : ArrayList<String> = arrayListOf(),
    @SerializedName("energy_saving" ) var energySaving : Int?              = null,
    @SerializedName("posted_by"     ) var postedBy     : String?           = null,
    @SerializedName("category"      ) var category     : String?           = null,
    @SerializedName("created_at"    ) var createdAt    : String?           = null

)
data class Categories (

    @SerializedName("id"  ) var id  : String? = null,
    @SerializedName("name" ) var name : String? = null

)