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

    @SerializedName("id"            ) var id           : String?           = null,
    @SerializedName("price"         ) var price        : Int?              = null,
    @SerializedName("used_for"      ) var usedFor      : String?           = null,
    @SerializedName("location_name" ) var locationName : String?           = null,
    @SerializedName("coordinates"   ) var coordinates  : Coordinates?      = Coordinates(),
    @SerializedName("status"        ) var status       : String?           = null,
    @SerializedName("condition"     ) var condition    : String?           = null,
    @SerializedName("brand"         ) var brand        : String?           = null,
    @SerializedName("cost_saving"   ) var costSaving   : Int?              = 0,
    @SerializedName("category"      ) var category     : Categories?         = Categories(),
    @SerializedName("description"   ) var description  : String?           = "",
    @SerializedName("images"        ) var images       : ArrayList<String> = arrayListOf(),
    @SerializedName("name"          ) var name         : String?           = "",
    @SerializedName("energy_saving" ) var energySaving : Int?              = 0,
    @SerializedName("posted_by"     ) var postedBy     : PostedBy?         = PostedBy(),
    @SerializedName("updated_at"    ) var updatedAt    : String?           = "",
    @SerializedName("created_at"    ) var createdAt    : String?           = "",
    @SerializedName("type"    ) var type    : String?           = "free",
    @SerializedName("currency"    ) var currency    : String?           = "INR"

)
data class Categories (

    @SerializedName("id"    ) var id    : String? = null,
    @SerializedName("image" ) var image : String? = null,
    @SerializedName("name"  ) var name  : String? = null

)
data class UserStats (

    @SerializedName("given"         ) var given        : Int? = null,
    @SerializedName("rating"        ) var rating       : Int? = null,
    @SerializedName("energy_saving" ) var energySaving : Int? = null,
    @SerializedName("cost_saving"   ) var costSaving   : Int? = null,
    @SerializedName("received"      ) var received     : Int? = null

)
data class PostedBy (

    @SerializedName("uid"          ) var uid         : String?    = null,
    @SerializedName("name"         ) var name        : String?    = null,
    @SerializedName("user_avatar"  ) var userAvatar  : String?    = null,
    @SerializedName("member_since" ) var memberSince : Int?       = null,
    @SerializedName("user_stats"   ) var userStats   : UserStats? = UserStats()

)