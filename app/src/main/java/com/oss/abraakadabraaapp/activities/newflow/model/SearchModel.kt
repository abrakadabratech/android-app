
import com.google.gson.annotations.SerializedName

data class SearchModel(
    @SerializedName("page"          ) var page         : Int?            = null,
    @SerializedName("count"         ) var count        : Int?            = null,
    @SerializedName("search_string" ) var searchString : String?         = null,
    @SerializedName("data"          ) var data         : ArrayList<Data> = arrayListOf()
)
data class Data (

    @SerializedName("id"            ) var id           : String? = null,
    @SerializedName("name"          ) var name         : String? = null,
    @SerializedName("location_name" ) var locationName : String? = null,
    @SerializedName("condition"     ) var condition    : String? = null,
    @SerializedName("distance"      ) var distance     : Int?    = null,
    @SerializedName("image"         ) var image        : String? = null

)