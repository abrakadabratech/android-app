
import com.google.gson.annotations.SerializedName


data class DataClass(val data: UsersUpdateData)

//Deprecated api request
data class UsersUpdateData (
    @Transient
    @SerializedName("email"            ) var email          : String? = "",
    @Transient
    @SerializedName("phone"            ) var phone          : String? = "",
    @SerializedName("name"             ) var name           : String? = "",
    @SerializedName("location"         ) var location            : Location? = Location() ,

)
data class Location(
    @SerializedName("lat"         ) var lat            : String? = "",
    @SerializedName("lng"         ) var lng            : String? = ""
){constructor():this("13.2343","17.31432")}
