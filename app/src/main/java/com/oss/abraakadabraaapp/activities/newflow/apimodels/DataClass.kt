
import com.google.gson.annotations.SerializedName


data class DataClass(val data: UsersUpdateData)

data class UsersUpdateData (
    @SerializedName("email"            ) var email          : String? = null,
    @SerializedName("name"             ) var name           : String? = null,
    @SerializedName("fcmToken"         ) var fcmToken            : String? = null
)