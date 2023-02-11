import com.google.gson.annotations.SerializedName
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UserStats

class RequestDetails(

    @SerializedName("code"   ) var code   : Int?  = null,
    @SerializedName("status" ) var status : Int?  = null,
    @SerializedName("data"   ) var data   : UserData? = UserData()
)

data class PostedByUser (

    @SerializedName("name"        ) var name       : String?    = null,
    @SerializedName("email"       ) var email      : String?    = null,
    @SerializedName("role"        ) var role       : String?    = null,
    @SerializedName("user_stats"  ) var userStats  : UserStats? = UserStats(),
    @SerializedName("phone"       ) var phone      : String?    = null,
    @SerializedName("user_avatar" ) var userAvatar : String?    = null,
    @SerializedName("id"          ) var id         : String?    = null

)
data class UserData (

    @SerializedName("energy_saving"   ) var energySaving   : Int?              = null,
    @SerializedName("used_for"        ) var usedFor        : String?           = null,
    @SerializedName("cost_saving"     ) var costSaving     : Int?              = null,
    @SerializedName("status"          ) var status         : String?           = null,
    @SerializedName("name"            ) var name           : String?           = null,
    @SerializedName("condition"       ) var condition      : String?           = null,
    @SerializedName("location_name"   ) var locationName   : String?           = null,
    @SerializedName("images"          ) var images         : ArrayList<String> = arrayListOf(),
    @SerializedName("category"        ) var category       : Categories1?           = null,
    @SerializedName("description"     ) var description    : String?           = null,
    @SerializedName("posted_by"       ) var postedBy       : PostedByUser?         = PostedByUser(),
    @SerializedName("request_status"  ) var requestStatus  : String?           = null,
    @SerializedName("product_id"      ) var productId      : String?           = null,
    @SerializedName("request_id"      ) var request_id      : String?           = null,
    @SerializedName("request_message" ) var requestMessage : String?           = null,
    @SerializedName("coordinates"   ) var coordinates  : Coordinates?      = Coordinates()

    )
data class Coordinates (

    @SerializedName("_latitude"  ) var Latitude  : Double? = null,
    @SerializedName("_longitude" ) var Longitude : Double? = null

)
data class Categories1 (

    @SerializedName("id"  ) var id  : String? = null,
    @SerializedName("name" ) var name : String? = null

)