package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData

data class AllCategoryResponse(

    @SerializedName("code"   ) var code   : Int?            = null,
    @SerializedName("status" ) var status : Int?            = null,
    @SerializedName("data"   ) var data   : ArrayList<UserCatData> = arrayListOf()

)
data class UserCatData (

    @SerializedName("id"    ) var id    : String? = null,
    @SerializedName("title" ) var title : String? = null,
    @SerializedName("image" ) var image : String? = null

)
