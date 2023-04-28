
import com.google.gson.annotations.SerializedName
import com.oss.abraakadabraaapp.datasource.products.Product

data class SearchModel(
    @SerializedName("code"   ) var code   : Int?  = null,
    @SerializedName("status" ) var status : Int?  = null,
    @SerializedName("data"   ) var data   : Data? = Data()
)
data class Data (

    @SerializedName("page"          ) var page         : String?             = null,
    @SerializedName("count"         ) var count        : Int?                = null,
    @SerializedName("search_string" ) var searchString : String?             = null,
    @SerializedName("products"      ) var products     : ArrayList<Product> = arrayListOf()

)
